package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class LevelActivity extends Activity {

    private ListView quizListView;
    private QuizListViewAdapter quizListViewAdapter;
    private int packageIndex;
    private int levelIndex;
    private int counter;
    private int time;
    private boolean isAnimating = false;
    private boolean hasDoneAnimation = false;
    private boolean hasWaited = false;
    private TextView back;
    private TextView textViewQuizSolved;

    private Handler m_handler;
    private Runnable m_handlerTask;

    private final static int COLUMNS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);

        //bundle
        packageIndex = getIntent().getExtras().getInt("packageIndex");
        levelIndex = getIntent().getExtras().getInt("levelIndex");

        switch (packageIndex) {
            case Utility.CINEMA:
                ((ImageView) (findViewById(R.id.backgroundImageView))).setImageDrawable(getResources().getDrawable(R.drawable.simpsons_background_complete));
                break;
            case Utility.MUSIC:
                ((ImageView) (findViewById(R.id.backgroundImageView))).setImageDrawable(getResources().getDrawable(R.drawable.guitar_background_complete));
                break;
            case Utility.VIP:

                break;
        }

        //binding
        quizListView = (ListView) findViewById(R.id.quizListView);
        back = (TextView) findViewById(R.id.back);
        textViewQuizSolved = (TextView) findViewById(R.id.textViewQuizDone);
        TextView textViewLevel = (TextView) findViewById(R.id.textViewLevel);

        //set adapter
        quizListViewAdapter = new QuizListViewAdapter(this, packageIndex, levelIndex);
        quizListView.setAdapter(quizListViewAdapter);

        textViewLevel.setText("Level " + (levelIndex + 1));
        textViewLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimationAppear();
            }
        });


        back.setText("< Levels");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getListView().setOnTouchListener(new AbsListView.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return isAnimating;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTextViewQuizSolved();
        refreshQuizGridAdapter();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && !hasDoneAnimation) {
            startAnimationAppear();
        }
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

    private void refreshTextViewQuizSolved() {
        int counter = 0;
        for (QuizData quizData : PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList()) {
            if (quizData.isSolved()) {
                counter++;
            }
        }
        textViewQuizSolved.setText(counter + " / 15");
    }


    public ListView getListView() {
        return quizListView;
    }

    private void initViewsToInvisible() {
        for (int i = 0; i < PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().size(); i++) {
            String path = "relativeLayout" + Integer.toString(i % COLUMNS + 1);
            int resId = LevelActivity.this.getResources().getIdentifier(path, "id", LevelActivity.this.getPackageName());
            try {
                View view = getListView().getChildAt(i / COLUMNS).findViewById(resId);
                view.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
            }
        }
    }


    private void startAnimationAppear() {
        isAnimating = true;
        hasDoneAnimation = true;
        initViewsToInvisible();
        counter = 0;
        time = 300;
        hasWaited = false;
        m_handler = new Handler();
        m_handlerTask = new Runnable() {
            @Override
            public void run() {
                String pathImage = "relativeLayout" + Integer.toString(counter % COLUMNS + 1);
                int resIdImage = LevelActivity.this.getResources().getIdentifier(pathImage, "id", LevelActivity.this.getPackageName());
                try {
                    if (hasWaited) {
                        time = 80;
                        View image = getListView().getChildAt(counter / COLUMNS).findViewById(resIdImage);
                        image.setVisibility(View.VISIBLE);
                        Animation anim = AnimationUtils.loadAnimation(LevelActivity.this, R.anim.anim_grow);
                        image.startAnimation(anim);
                        counter++;
                    } else {
                        hasWaited = true;
                    }
                    m_handler.postDelayed(m_handlerTask, time);
                } catch (Exception e) {
                    isAnimating = false;
                }
            }
        };
        m_handlerTask.run();
    }


}
