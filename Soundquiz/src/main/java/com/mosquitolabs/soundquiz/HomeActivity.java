package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;

public class HomeActivity extends Activity {

    private Button playButton;
    private ProgressBar progressBar;

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
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);
        initViews();
        AudioPlayer.getIstance().initSounds(this);
        PackageCollection.getInstance().populateCollection(this);
    }

    private void initViews() {
        playButton = (Button) findViewById(R.id.playButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.imageViewTitle).getLayoutParams().width = Utility.getWidth(this) * 2 / 3;
    }

    public void initHomePage() {

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPackageListActivity();
            }
        });
        playButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

//        debugShortCut();
//        startPackageListActivity();
    }


    private void debugShortCut() {
        Intent mIntent = new Intent(this, QuizActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("quizIndex", 1);
        bundle.putInt("levelIndex", 0);
        bundle.putInt("packageIndex", Utility.VIP);
        mIntent.putExtras(bundle);

        startActivity(mIntent);
    }


}
