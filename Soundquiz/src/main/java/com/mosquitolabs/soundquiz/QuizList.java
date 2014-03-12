package com.mosquitolabs.soundquiz;

import android.content.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by francesco on 3/10/14.
 */
public class QuizList {
    private String category;
    private String path;
    private ArrayList<QuizData> quizList = new ArrayList<QuizData>();

    public void setPath(String newPath) {
        path = newPath;
        setCategory(path.substring(3));
    }

    private void setCategory(String cat){
        category = cat;
    }

    public String getCategory(){
        return category;
    }

    public ArrayList<QuizData> getQuizList(){
        return quizList;
    }

    public void setQuizListFromDisk(Context paramActivity, String path) {
        try {
            setPath(path);
            ObjectInputStream localObjectInputStream = new ObjectInputStream(
                    paramActivity.openFileInput(path));
            ArrayList localArrayList = (ArrayList) localObjectInputStream
                    .readObject();
            localObjectInputStream.close();
            quizList = localArrayList;
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        } catch (ClassNotFoundException localClassNotFoundException) {
            localClassNotFoundException.printStackTrace();
        }
    }


    public void saveQuizListToDisk(Context paramActivity) {
        try {
            ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(
                    paramActivity.openFileOutput(path, 0));
            localObjectOutputStream.writeObject(quizList);
            localObjectOutputStream.close();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }



}
