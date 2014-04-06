package com.mosquitolabs.soundquiz;

import java.util.ArrayList;

/**
 * Created by francesco on 3/14/14.
 */
public class LevelData {

    private String category;
    private boolean isFree = true;

    private ArrayList<QuizData> quizList = new ArrayList<QuizData>();


    public void setCategory(String cat) {
        category = cat;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<QuizData> getQuizList() {
        return quizList;
    }

    public void setFree(boolean isFree){
        this.isFree = isFree;
    }

    public boolean isFree(){
        return isFree;
    }


}
