package com.mosquitolabs.soundquiz;

import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by francesco on 3/10/14.
 */
public class QuizData implements Serializable {
    private ArrayList<String> answers = new ArrayList<String>();
    private String ID;
    private String quizID;
    private String wikiID;
    private String wikiDescription;
    private String category;
    private boolean isSolved = false;
    private boolean isSolvedWithStar = false;
    private boolean hasUsedHint = false;

    public void addAnswer(String newAnswer) {
        answers.add(newAnswer);
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setID(String string) {
        ID = string;
    }

    public String getID() {
        return ID;
    }

    public void setQuizID(String string) {
        quizID
                = string;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setCategory(String string) {
        category = string;
    }

    public String getCategory() {
        return category;
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
        return (isSolved() && !hasUsedHint());
    }

    public void setSolved() {
        isSolved = true;
    }

    public void updateSharedPreferences(String category, int levelIndex) {
        String ID = category + Integer.toString(levelIndex) + getID();
        SharedPreferences.Editor editor = Utility.getSharedPreferences().edit();
        editor.putBoolean(ID + "_is_solved", isSolved());
        editor.putBoolean(ID + "_has_used_hint", hasUsedHint());
        editor.commit();
    }


}
