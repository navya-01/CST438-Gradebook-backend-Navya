package com.cst438;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class UserAlias {
    /*
     * values for user level
     */
    public static final String NONE = "none";
    public static final String FIRST = "FirstCorrect";
    public static final String FIVE = "5-in-a-row";
    public static final String MASTERY = "Mastery";

    @Id
    private String alias; // alias name
    private int attempts;
    private int correct;
    private int consecutive_correct;
    private String level = NONE;

    public String getAlias() {
        return alias;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getConsecutive_correct() {
        return consecutive_correct;
    }

    public void setConsecutive_correct(int consecutive_correct) {
        this.consecutive_correct = consecutive_correct;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setAlias(String alias) {
    }
}