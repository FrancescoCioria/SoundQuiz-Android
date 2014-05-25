package com.mosquitolabs.soundquiz;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by francesco on 3/10/14.
 */
public class QuizData implements Serializable {
    private ArrayList<String> rows = new ArrayList<String>();
    private String answer;
    private String ID;
    private String type;
    private String wikiURI;
    private String wikiDescription;
    private String spotifyURI;
    private boolean isSolved = false;

    private boolean hasUsedHint = false;

    public void setAnswer(String newAnswer) {
        answer = newAnswer;
    }

    public void addRow(String newRow) {
        rows.add(newRow);
    }

    public String getAnswer() {
        return answer;
    }

    public ArrayList<String> getRows() {
        return rows;
    }

    public void setID(String string) {
        ID = string;
    }

    public String getID() {
        return ID;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setWikiURI(String wikiURI) {
        this.wikiURI = wikiURI;
    }

    public void setWikiDescription(String wikiDescription) {
        this.wikiDescription = wikiDescription;
    }

    public String getWikiURI() {
        return wikiURI;
    }

    public String getWikiDescription() {
        return wikiDescription;
    }

    public void setSpotifyURI(String spotifyURI) {
        this.spotifyURI = spotifyURI;
    }

    public String getSpotifyURI() {
        return spotifyURI;
    }

    public String getType() {
        return type;
    }


}
