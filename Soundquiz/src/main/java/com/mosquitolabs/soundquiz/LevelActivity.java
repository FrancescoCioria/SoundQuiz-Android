package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class LevelActivity extends Activity {

    private ListView quizListView;
    private QuizListViewAdapter quizListViewAdapter;
    private int packageIndex;
    private int levelIndex;
    private int width;
    private int height;


    private final static int MIN_GAP = 10;
    private final static int MAX_WIDTH = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;


        //bundle
        packageIndex = getIntent().getExtras().getInt("packageIndex");
        levelIndex = getIntent().getExtras().getInt("levelIndex");

        //binding
        quizListView = (ListView) findViewById(R.id.quizListView);

        //set adapter
        quizListViewAdapter = new QuizListViewAdapter(this, packageIndex, levelIndex);
        quizListView.setAdapter(quizListViewAdapter);


        getActionBar().setTitle(PackageCollection.getInstance().getPackageCollection().get(packageIndex).getCategory() + ": Level " + Integer.toString(levelIndex + 1));
        getActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshQuizGridAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void refreshQuizGridAdapter() {
        if (quizListViewAdapter != null) {
            LevelActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    quizListViewAdapter.notifyDataSetChanged();
                }
            });
        }
    }



    public ListView getListView(){
        return quizListView;
    }


}
