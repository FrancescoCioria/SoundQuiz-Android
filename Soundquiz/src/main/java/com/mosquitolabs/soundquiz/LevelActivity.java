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
import android.widget.ListView;
import android.widget.TextView;


public class LevelActivity extends Activity {

    private ListView quizListView;
    private QuizListViewAdapter quizListViewAdapter;
    private int packageIndex;
    private int levelIndex;
    private int width;
    private int height;
    private int counter;
    private int time;
    private boolean isAnimating = false;
    private boolean hasDoneAnimation = false;
    private boolean hasWaited = false;
    private TextView back;

    private Handler m_handler;
    private Runnable m_handlerTask;

    private Animation grow;

    private final static int MIN_GAP = 10;
    private final static int MAX_WIDTH = 300;
    private final static int COLUMNS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        width = Utility.getWidth(this);
        height = Utility.getHeight(this);

        //bundle
        packageIndex = getIntent().getExtras().getInt("packageIndex");
        levelIndex = getIntent().getExtras().getInt("levelIndex");

        //binding
        quizListView = (ListView) findViewById(R.id.quizListView);
        back = (TextView) findViewById(R.id.back);

        //set adapter
        quizListViewAdapter = new QuizListViewAdapter(this, packageIndex, levelIndex);
        quizListView.setAdapter(quizListViewAdapter);


        findViewById(R.id.textViewLevel).setOnClickListener(new View.OnClickListener() {
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

        getActionBar().setTitle(PackageCollection.getInstance().getPackageCollection().get(packageIndex).getCategory() + ": Level " + Integer.toString(levelIndex + 1));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();

        getListView().setOnTouchListener(new AbsListView.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return isAnimating;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        time = 400;
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
