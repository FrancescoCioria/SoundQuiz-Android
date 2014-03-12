package com.mosquitolabs.soundquiz;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by francesco on 3/10/14.
 */

public class QuizCollection {
    private static QuizCollection instance = new QuizCollection();
    private ArrayList<QuizList> quizCollection = new ArrayList<QuizList>();

    public static QuizCollection getInstance() {
        return instance;
    }


    public ArrayList<QuizList> getQuizCollection() {
        return quizCollection;
    }

    public void getQuizCollectionFromDisk(final Context context) {
        ArrayList<String> files = new ArrayList<String>();
        File directory = context.getFilesDir();
        try {
            File[] tempfiles = directory.listFiles();

            for (int i = 0; i < tempfiles.length; ++i) {
                files.add(tempfiles[i].getName());
            }

            for (String path : files) {

                String key = path.substring(0, 3);
                if (key.equals("lst")) {
                    QuizList quizList = new QuizList();
                    quizList.setQuizListFromDisk(context,path);
                    quizCollection.add(quizList);
                }


            }
        } catch (Exception e) {

        }
    }

    public void saveQuizCollectionToDisk(Context context){
        for(QuizList quizList : getQuizCollection()){
            quizList.saveQuizListToDisk(context);
        }
    }


}
