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
    private JSONArray savedData = null;

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
                Utility.initUtility(context);
                Log.d("POPULATE", "populating");
                PackageCollection.this.context = context;
                long startTime = System.currentTimeMillis();

                // populate
                populate();

                Log.d("POPULATE", "time: " + (System.currentTimeMillis() - startTime));

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
//            String mainJsonString = getJsonFromS3(MAIN_BUCKET, "main.json");
            String mainJsonString = getJsonFromRaw("main");
            editor.putString("main_json", mainJsonString);

//            read main.json and create every category.json
            JSONObject mainJSON = new JSONObject(mainJsonString);
            for (int i = 0; i < mainJSON.getJSONArray("categories").length(); i++) {
                String category = mainJSON.getJSONArray("categories").getJSONObject(i).getString("category").toLowerCase();
//                String categoryJson = getJsonFromS3(MAIN_BUCKET, category + ".json");
                String categoryJson = getJsonFromRaw(category);
                editor.putString(category + "_json", categoryJson);
            }
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

//    private void populate() throws IOException {
//        Log.d("POPULATE", "populatenew");
//        JsonReader reader = null;
//        try {
//            JSONObject mainJSON = new JSONObject(Utility.getSharedPreferences().getString("main_json", "{}"));
//            JSONArray categories = mainJSON.getJSONArray("categories");
//
////            for each category get its json and populate
//            for (int x = 0; x < categories.length(); x++) {
//                String json = Utility.getSharedPreferences().getString(categories.getJSONObject(x).getString("category") + "_json", "{}");
//                byte[] bytes = json.getBytes();
//                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//                InputStreamReader isr = new InputStreamReader(bais);
//                reader = new JsonReader(isr);
//
//                String category = "";
//                reader.beginObject();
//                while (reader.hasNext()) {
//                    String name = reader.nextName();
//                    if (name.equals("category")) {
//                        category = reader.nextString();
//                    } else if (name.equals("levels")) {
//                        PackageCollection.getInstance().getPackageCollection().add(iterateLevels(reader, category));
//                    } else {
//                        reader.skipValue();
//                    }
//                }
//                reader.endObject();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        } finally {
//            reader.close();
//        }
//
//    }
//
//    private PackageData iterateLevels(JsonReader reader, String category) throws IOException {
//        PackageData packageData = new PackageData();
//        packageData.setCategory(category);
//        reader.beginArray();
//        while (reader.hasNext()) {
//            reader.beginObject();
//            while (reader.hasNext()) {
//                String name = reader.nextName();
//                if (name.equals("quizzes")) {
//                    packageData.getLevelList().add(iterateQuizzes(reader));
//                } else {
//                    reader.skipValue();
//                }
//            }
//            reader.endObject();
//        }
//        reader.endArray();
//
//        return packageData;
//    }
//
//    private LevelData iterateQuizzes(JsonReader reader) throws IOException {
//        LevelData levelData = new LevelData();
//        reader.beginArray();
//        while (reader.hasNext()) {
//            reader.beginObject();
//            QuizData quizData = new QuizData();
//            while (reader.hasNext()) {
//                String name = reader.nextName();
//                if (name.equals("id")) {
//                    quizData.setID(reader.nextString());
//                } else if (name.equals("type")) {
//                    quizData.setType(reader.nextString());
//                } else if (name.equals("answer")) {
//                    quizData.setAnswer(reader.nextString());
//                } else if (name.equals("rows")) {
//                    iterateRows(reader, quizData);
//                } else {
//                    reader.skipValue();
//                }
//            }
//            if (!addQuizToSavedData(quizData)) {
//                populateQuizWithSavedData(quizData);
//            }
//
//            levelData.getQuizList().add(quizData);
//            reader.endObject();
//        }
//        reader.endArray();
//
//        return levelData;
//
//    }
//
//    private void iterateRows(JsonReader reader, QuizData quizData) throws IOException {
//        reader.beginArray();
//        while (reader.hasNext()) {
//            String row = reader.nextString();
//            quizData.addRow(row);
//        }
//        reader.endArray();
//    }

    private void populate() {
        try {
//            read main.json and create every category.json
            String mainJsonString = getJsonFromRaw("main");
            JSONObject mainJSON = new JSONObject(mainJsonString);
            JSONArray categories = mainJSON.getJSONArray("categories");
//            for each category get its json and populate
            for (int x = 0; x < categories.length(); x++) {
                String category = categories.getJSONObject(x).getString("category").toLowerCase();
//                String categoryJson = getJsonFromS3(MAIN_BUCKET, category + ".json");
                String categoryJson = getJsonFromRaw(category);
                JSONObject categoryJSON = new JSONObject(categoryJson);
                PackageData packageData = new PackageData();
                JSONArray levels = categoryJSON.getJSONArray("levels");
                for (int i = 0; i < levels.length(); i++) {
                    LevelData levelData = new LevelData();
                    JSONObject levelJSON = levels.getJSONObject(i);

                    levelData.setFree(levelJSON.getBoolean("is_free"));
                    JSONArray quizzes = levelJSON.getJSONArray("quizzes");
                    for (int z = 0; z < quizzes.length(); z++) {
                        QuizData quizData = new QuizData();
                        JSONObject quizJSON = quizzes.getJSONObject(z);

                        quizData.setAnswer(quizJSON.getString("answer"));
                        JSONArray rows = quizJSON.getJSONArray("rows");
                        if (rows.length() == 0) {
                            quizData.addRow(quizData.getAnswer());
                        } else {
                            for (int y = 0; y < rows.length(); y++) {
                                quizData.addRow(rows.getString(y));
                            }
                        }

                        quizData.setID(quizJSON.getString("id"));

                        quizData.setType(quizJSON.getString("type"));

                        if (!addQuizToSavedData(quizData)) {
                            populateQuizWithSavedData(quizData);
                        }

                        levelData.getQuizList().add(quizData);
                    }
                    packageData.getLevelList().add(levelData);
                    if(category.equals("cinema_tv")){
                        packageData.setCategory("THEMES");
                    }else if(category.equals("music")){
                        packageData.setCategory("MUSIC");
                    }else{
                        packageData.setCategory("VOICES");
                    }
                }

                PackageCollection.getInstance().getPackageCollection().add(packageData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public boolean addQuizToSavedData(QuizData quizData) {
        try {
            if (savedData == null) {
                savedData = new JSONArray(Utility.getSharedPreferences().getString("saved_data", "[]"));
            }
            for (int i = 0; i < savedData.length(); i++) {
                if (savedData.getJSONObject(i).getString("id").equals(quizData.getID())) {
                    return false;
                }
            }

            Log.d("PACKAGE_COLLECTION", "adding quiz to savedData");

            savedData.put(new JSONObject().put("id", quizData.getID()).put("has_used_hint", false).put("is_solved", false));
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
            if (savedData == null) {
                savedData = new JSONArray(Utility.getSharedPreferences().getString("saved_data", "[]"));
            }
            for (int i = 0; i < savedData.length(); i++) {
                if (savedData.getJSONObject(i).getString("id").equals(quizData.getID())) {
                    savedData.put(i, new JSONObject().put("id", quizData.getID()).put("has_used_hint", quizData.hasUsedHint()).put("is_solved", quizData.isSolved()));
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
            if (savedData == null) {
                savedData = new JSONArray(Utility.getSharedPreferences().getString("saved_data", "[]"));
            }
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

    private String getJsonFromRaw(String jsonPath) {
        String JSONString;
        try {
            //open the inputStream to the file
            int res = context.getResources().getIdentifier(jsonPath, "raw", context.getPackageName());
            InputStream inputStream = context.getResources().openRawResource(res);

            int sizeOfJSONFile = inputStream.available();

            //array that will store all the data
            byte[] bytes = new byte[sizeOfJSONFile];

            //reading data into the array from the file
            inputStream.read(bytes);

            //close the input stream
            inputStream.close();

            JSONString = new String(bytes, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return JSONString;

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
