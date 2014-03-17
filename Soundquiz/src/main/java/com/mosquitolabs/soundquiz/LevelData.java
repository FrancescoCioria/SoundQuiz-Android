package com.mosquitolabs.soundquiz;

import java.util.ArrayList;

/**
 * Created by francesco on 3/14/14.
 */
public class LevelData {

    private String category;

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

//    public void setLevelFromDisk(Context paramActivity, String path) {
//        try {
//            setPath(path);
//            ObjectInputStream localObjectInputStream = new ObjectInputStream(
//                    paramActivity.openFileInput(path));
//            ArrayList localArrayList = (ArrayList) localObjectInputStream
//                    .readObject();
//            localObjectInputStream.close();
//            quizList = localArrayList;
//            return;
//        } catch (IOException localIOException) {
//            localIOException.printStackTrace();
//        } catch (ClassNotFoundException localClassNotFoundException) {
//            localClassNotFoundException.printStackTrace();
//        }
//    }
//
//
//    public void saveLevelToDisk(Context paramActivity) {
//        try {
//            ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(
//                    paramActivity.openFileOutput(path, 0));
//            localObjectOutputStream.writeObject(quizList);
//            localObjectOutputStream.close();
//            return;
//        } catch (IOException localIOException) {
//            localIOException.printStackTrace();
//        }
//    }


}
