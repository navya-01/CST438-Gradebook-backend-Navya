package com.cst438;
import javax.transaction.Transactional;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.cst438.dto.MultiplyResult;
import com.cst438.dto.UserLevel;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class MultiplyLevelService {

    /*
     * create or use existing message queue
     */
    Queue queue = new Queue("multiply-game", true);

    @Bean
    public Queue createQueue() {
        return new Queue("multiply-level", true);
    }
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    UserAliasRepository userAliasRepository;

    /*
     * read messages from multiply-level queue
     * update user's level
     * send reply message back to MultiplyGame
     */
    @RabbitListener(queues = "multiply-level")
    @Transactional
    public void receiveMessageFromGame(String data ) {
        System.out.println("Received from Game: "+data);
        MultiplyResult mr = fromJsonString(data, MultiplyResult.class);
        UserLevel userLevel = doRequest(mr);
        String str = asJsonString(userLevel);
        rabbitTemplate.convertAndSend(queue.getName(), str);
        System.out.println("Message sent: str");
    }

    public  UserLevel doRequest(MultiplyResult mr) {
        // update database
        UserAlias ua = userAliasRepository.findByAlias(mr.alias());
        if (ua==null) {
            ua = new UserAlias();
            ua.setAlias(mr.alias());
        }
        int attempts = ua.getAttempts();
        attempts++;
        ua.setAttempts(attempts);
        if (mr.correct()) {
            int correct = ua.getCorrect();
            correct++;
            ua.setCorrect(correct);
            // is this the first correct?
            if (ua.getLevel().equals(UserAlias.NONE))
                ua.setLevel(UserAlias.FIRST);
            ua.setConsecutive_correct(ua.getConsecutive_correct()+1);
            // is this 5 correct in a row?
            if (ua.getConsecutive_correct() >= 5 &&

                    ua.getLevel().equals(UserAlias.FIRST)) {
                ua.setLevel(UserAlias.FIVE);
            }
            // at least 25 attempts with 90% correct, then MASTERY
            if (attempts >= 25 && correct > 22)
                ua.setLevel(UserAlias.MASTERY);
        } else {
            ua.setConsecutive_correct(0);
        }
        userAliasRepository.save(ua);
        UserLevel userLevel = new UserLevel(
                ua.getAlias(),
                ua.getLevel());
        return userLevel;
    }
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
