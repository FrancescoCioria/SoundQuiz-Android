package com.mosquitolabs.soundquiz;

import java.io.Serializable;

/**
 * Created by francesco on 3/10/14.
 */
public class QuizData implements Serializable {
    private String answer;
    private String categroy;
    private boolean isSolved = false;
    private boolean isSolvedWithStar = false;
    private boolean hasUsedHint = false;

    public void setAnswer(String string) {
        answer = string;
    }

    public String getAnswer() {
        return answer;
    }

    public void setCategory(String string) {
        categroy = string;
    }

    public String getCategroy() {
        return categroy;
    }

    public void setUsedHint() {
        hasUsedHint = true;
    }

    public boolean hasUsedHint() {
        return hasUsedHint;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public boolean isSolvedWithStar() {
        return isSolvedWithStar;
    }

    public void setSolvedStatus(boolean status) {
        isSolved = status;
    }

    public void setSolvedWithStar(boolean status) {
        isSolvedWithStar = status;
    }
}
