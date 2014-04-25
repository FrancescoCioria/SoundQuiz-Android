package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class PackageActivity extends Activity {

    private int counter;
    private int time;
    private boolean isAnimating = false;
    private boolean hasDoneAnimation = false;
    private boolean hasWaited = false;

    private int packageIndex;

    private Handler m_handler;
    private Runnable m_handlerTask;

    private ListView levelListView;

    private LevelListViewAdapter levelListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);
        setContentView(R.layout.activity_package);

        packageIndex = getIntent().getExtras().getInt("packageIndex");

        switch (packageIndex) {
            case Utility.CINEMA:
                ((ImageView)(findViewById(R.id.backgroundImageView))).setImageDrawable(getResources().getDrawable(R.drawable.simpsons_background_complete));
                break;
            case Utility.MUSIC:
                ((ImageView)(findViewById(R.id.backgroundImageView))).setImageDrawable(getResources().getDrawable(R.drawable.guitar_background_complete));
                break;
            case Utility.VIP:
                ((ImageView)(findViewById(R.id.backgroundImageView))).setImageDrawable(getResources().getDrawable(R.drawable.guess_who_background_complete));
                break;
        }


        levelListView = (ListView) findViewById(R.id.levelListView);
//        TextView back = (TextView) findViewById(R.id.back);
        RelativeLayout topBar = (RelativeLayout) findViewById(R.id.topBar);

        levelListViewAdapter = new LevelListViewAdapter(this, packageIndex);
        levelListView.setAdapter(levelListViewAdapter);

//        topBar.getLayoutParams().width = Utility.getWidth(this) * 9 / 10;

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.back).setAlpha(0.5f);
        findViewById(R.id.back).getLayoutParams().height = Utility.convertDpToPixels(this, 50) * 2 / 3;
        findViewById(R.id.back).getLayoutParams().width = (int) (Utility.convertDpToPixels(this, 50) * 0.666f * 2.25f);


//        back.setText("Categories");
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        levelListView.setOnTouchListener(new AbsListView.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return isAnimating;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapter();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && !hasDoneAnimation) {
            startAnimationAppear();
        }
    }


    private void initViewsToInvisible() {
        for (int i = 0; i < PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().size(); i++) {
            try {
                View view = levelListView.getChildAt(i).findViewById(R.id.layout);
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
        time = 200;
        hasWaited = false;
        m_handler = new Handler();
        m_handlerTask = new Runnable() {
            @Override
            public void run() {
                try {
                    if (hasWaited) {
                        time = 80;
                        View image = levelListView.getChildAt(counter).findViewById(R.id.layout);
                        image.setVisibility(View.VISIBLE);
                        Animation anim = AnimationUtils.loadAnimation(PackageActivity.this, R.anim.anim_grow);
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

    private void refreshAdapter() {
        levelListViewAdapter.notifyDataSetChanged();
    }

}
