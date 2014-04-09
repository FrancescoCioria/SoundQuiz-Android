package com.mosquitolabs.soundquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by francesco on 3/10/14.
 */

public class PackageCollection {
    private static PackageCollection instance = new PackageCollection();
    private ArrayList<PackageData> packageCollection = new ArrayList<PackageData>();

    private final String MAIN_BUCKET = "soundquiz";
    private final String IMAGES_BUCKET = "soundquiz/pictures";
    private final String SOUNDS_BUCKET = "soundquiz/sounds";

    private Context context;

    public static PackageCollection getInstance() {
        return instance;
    }


    public ArrayList<PackageData> getPackageCollection() {
        return packageCollection;
    }

    public void populateCollection(final Context context) {

        AsyncTask<Void, Integer, Bitmap> task = new AsyncTask<Void, Integer, Bitmap>() {

            @Override
            public Bitmap doInBackground(Void... params) {

                Log.d("RUNNABLE", "running");
                PackageCollection.this.context = context;
                long startTime = System.currentTimeMillis();
                String jsonString = Utility.getSharedPreferences().getString("main_json", "");
                if (jsonString.length() == 0) {
                    // first time
                    initJsonFiles();
                }
                // populate
                populate();

                // show opening logo at least 2 seconds
                while (System.currentTimeMillis() - startTime < 2000) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                ((HomeActivity) context).initHomePage();
            }
        };
        task.execute();
    }

    private void initJsonFiles() {
        try {
            SharedPreferences.Editor editor = Utility.getSharedPreferences().edit();
            String mainJsonString = getJsonFromS3(MAIN_BUCKET, "main.json");
            editor.putString("main_json", mainJsonString);

//            read main.json and create every category.json
            JSONObject mainJSON = new JSONObject(mainJsonString);
            for (int i = 0; i < mainJSON.getJSONArray("categories").length(); i++) {
                String category = mainJSON.getJSONArray("categories").getJSONObject(i).getString("category").toLowerCase();
                String categoryJson = getJsonFromS3(MAIN_BUCKET, category + ".json");
                editor.putString(category + "_json", categoryJson);
            }

            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populate() {
        try {
            JSONObject mainJSON = new JSONObject(Utility.getSharedPreferences().getString("main_json", "{}"));
            JSONArray categories = mainJSON.getJSONArray("categories");

//            for each category get its json and populate
            for (int x = 0; x < categories.length(); x++) {
                JSONObject categoryJSON = new JSONObject(Utility.getSharedPreferences().getString(categories.getJSONObject(x).getString("category") + "_json", "{}"));
                PackageData packageData = new PackageData();
                String category = categoryJSON.getString("category");
                JSONArray levels = categoryJSON.getJSONArray("levels");
                for (int i = 0; i < levels.length(); i++) {
                    LevelData levelData = new LevelData();
                    JSONObject levelJSON = levels.getJSONObject(i);

                    levelData.setFree(levelJSON.getBoolean("is_free"));
                    JSONArray quizzes = levelJSON.getJSONArray("quizzes");
                    for (int z = 0; z < quizzes.length(); z++) {
                        QuizData quizData = new QuizData();
                        JSONObject quizJSON = quizzes.getJSONObject(z);

                        JSONArray answers = quizJSON.getJSONArray("answers");
                        for (int y = 0; y < answers.length(); y++) {
                            quizData.addAnswer(answers.getString(y));
                        }
                        JSONArray rows = quizJSON.getJSONArray("rows");
                        if (rows.length() == 0) {
                            quizData.addRow(quizData.getAnswers().get(0));
                        } else {
                            for (int y = 0; y < rows.length(); y++) {
                                quizData.addRow(rows.getString(y));
                            }
                        }

                        String ID = category + Integer.toString(i) + quizJSON.getString("id");
                        quizData.setID(ID);
                        quizData.setQuizID(quizJSON.getString("id"));

                        if (!addQuizToSavedData(quizData)) {
                            populateQuizWithSavedData(quizData);
                        }
//                        if (Utility.readImageFromDisk(context, ID, true) == null) {
//                            Utility.saveImageToDisk(context, ID, getImageFromS3(quizData.getQuizID(),false));
//                            Utility.saveImageToDisk(context, ID, getImageFromS3(quizData.getQuizID(),true));
//                        }

                        levelData.getQuizList().add(quizData);
                    }
                    packageData.getLevelList().add(levelData);
                    packageData.setCategory(category);
                }

                Log.d("SAVED_DATA", Utility.getSharedPreferences().getString("saved_data", "[]"));
                PackageCollection.getInstance().getPackageCollection().add(packageData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public boolean addQuizToSavedData(QuizData quizData) {
        try {
            JSONArray savedData = new JSONArray(Utility.getSharedPreferences().getString("saved_data", "[]"));
            for (int i = 0; i < savedData.length(); i++) {
                if (savedData.getJSONObject(i).getString("id").equals(quizData.getID())) {
                    return false;
                }
            }
            JSONObject newQuizData = new JSONObject();
            newQuizData.put("id", quizData.getID());
            newQuizData.put("has_used_hint", false);
            newQuizData.put("is_solved", false);
            savedData.put(newQuizData);
            SharedPreferences.Editor editor = Utility.getSharedPreferences().edit();
            editor.putString("saved_data", savedData.toString());
            editor.commit();
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void modifyQuizInSavedData(QuizData quizData) {
        try {
            JSONArray savedData = new JSONArray(Utility.getSharedPreferences().getString("saved_data", "[]"));
            for (int i = 0; i < savedData.length(); i++) {
                if (savedData.getJSONObject(i).getString("id").equals(quizData.getID())) {
                    JSONObject newQuizData = new JSONObject();
                    newQuizData.put("id", quizData.getID());
                    newQuizData.put("has_used_hint", quizData.hasUsedHint());
                    newQuizData.put("is_solved", quizData.isSolved());
                    savedData.put(i, newQuizData);
                    SharedPreferences.Editor editor = Utility.getSharedPreferences().edit();
                    editor.putString("saved_data", savedData.toString());
                    editor.commit();
                    return;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void populateQuizWithSavedData(QuizData quizData) {
        try {
            JSONArray savedData = new JSONArray(Utility.getSharedPreferences().getString("saved_data", "[]"));
            for (int i = 0; i < savedData.length(); i++) {
                if (savedData.getJSONObject(i).getString("id").equals(quizData.getID())) {
                    if (savedData.getJSONObject(i).getBoolean("has_used_hint")) {
                        quizData.setUsedHint();
                    }
                    if (savedData.getJSONObject(i).getBoolean("is_solved")) {
                        quizData.setSolved();
                    }
                    return;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJsonFromS3(String bucket, String name) {
        try {
            S3Object object = Utility.getS3Client().getObject(new GetObjectRequest(bucket, name));
            InputStream reader = new BufferedInputStream(
                    object.getObjectContent());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[8192];

            int numRead = 0;
            while ((numRead = reader.read(bytes)) >= 0) {
                baos.write(bytes, 0, numRead);
            }
            byte[] buffer = baos.toByteArray();
            reader.close();
            return new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getImageFromS3(String ID, boolean blurred) {
        String path = blurred ? ID + "_blur" + ".png" : ID + ".png";
        S3Object o = Utility.getS3Client().getObject(IMAGES_BUCKET, path);
        InputStream is = o.getObjectContent();

        Bitmap image = null;

        try {
            // Use the inputStream and close it after.
            image = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("IMAGE", ID);
            }
        }

        return image;
    }


}
