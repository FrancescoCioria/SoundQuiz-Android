package com.mosquitolabs.soundquiz;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by francesco on 3/10/14.
 */

public class PackageCollection {
    private static PackageCollection instance = new PackageCollection();
    private ArrayList<PackageData> packageCollection = new ArrayList<PackageData>();

    public static PackageCollection getInstance() {
        return instance;
    }


    public ArrayList<PackageData> getPackageCollection() {
        return packageCollection;
    }

    public void getPackageCollectionFromDisk(final Context context) {
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
                    PackageData quizList = new PackageData();
                    packageCollection.add(quizList);
                }


            }
        } catch (Exception e) {

        }
    }

//    public void saveQuizCollectionToDisk(Context context){
//        for(QuizList quizList : getQuizCollection()){
//            quizList.saveQuizListToDisk(context);
//        }
//    }


}
