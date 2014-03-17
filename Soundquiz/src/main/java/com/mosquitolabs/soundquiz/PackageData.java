package com.mosquitolabs.soundquiz;

import java.util.ArrayList;

/**
 * Created by francesco on 3/14/14.
 */
public class PackageData {

    private String category;
    private String path;

    private ArrayList<LevelData> levelList = new ArrayList<LevelData>();

    public ArrayList<LevelData> getLevelList() {
        return levelList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String cat) {
        category = cat;
    }


}
