package com.cst438;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cst438.dto.UserLevel;
import com.cst438.dto.MultiplyResult;
@RestController
public class MultiplyLevelController {

    @Autowired
    UserAliasRepository userAliasRepository;

    @Autowired
    MultiplyLevelService mlService;

    @PostMapping("/multiply_level")
    public UserLevel postResult(@RequestBody MultiplyResult mr) {
        System.out.println("Level Controller post:"+mr);
        UserLevel userLevel = mlService.doRequest(mr);
        return userLevel;
    }
}
