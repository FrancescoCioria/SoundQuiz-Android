package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;

public class HomeActivity extends Activity {

    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOpeningLogoPage();
    }

    private void startPackageListActivity() {
        Intent mIntent = new Intent(this, PackageListActivity.class);
        this.startActivity(mIntent);
    }

    private void initOpeningLogoPage() {
        setContentView(R.layout.activity_home_opening_logo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);
        AudioPlayer.getIstance().initSounds(this);
        populate();
    }

    public void initHomePage() {
        setContentView(R.layout.activity_home);
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPackageListActivity();
            }
        });

//        debugShortCut();
        startPackageListActivity();
    }


    private void populate() {
        Utility.initUtility(this);
        PackageCollection.getInstance().populateCollection(this);
    }


    private void debugShortCut() {
        Intent mIntent = new Intent(this, QuizActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("quizIndex", 0);
        bundle.putInt("levelIndex", 0);
        bundle.putInt("packageIndex", 1);
        mIntent.putExtras(bundle);

        startActivity(mIntent);
    }


}
