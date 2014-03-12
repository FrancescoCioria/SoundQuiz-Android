package com.mosquitolabs.soundquiz;

import java.io.Serializable;

/**
 * Created by francesco on 3/10/14.
 */
public class QuizData implements Serializable {
    private String answer;
    private String categroy;
    private int remainingHints = 3;
    private int possibleScore = 100;
    private boolean isSolved = false;

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

    public void decreaseRemainingHintsByOne() {
        if (remainingHints > 0) {
            remainingHints--;
        }
    }

    public int getRemainingHints() {
        return remainingHints;
    }

    public void resetRemainingHints() {
        remainingHints = 3;
    }

    public int getPossibleScore() {
        return possibleScore;
    }

    public void setPossibleScore(int newPossibleScore) {
        possibleScore = newPossibleScore;
    }

    public boolean isSolved(){
        return isSolved;
    }
    public void setSolvedStatus(boolean status){
        isSolved = status;
    }
}
