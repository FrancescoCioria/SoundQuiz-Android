package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;

import java.util.Random;

public class HomeActivity extends Activity {

    private Button playButton;
    private int cyclesLeft = 0;
    Random random = new Random();

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

//        southParkTest();
//        startLoop();


//        debugShortCut();
//        startPackageListActivity();
    }


    private void startLoop() {
        final Drawable mouthShapes[] = {getResources().getDrawable(R.drawable.ah), getResources().getDrawable(R.drawable.eh), getResources().getDrawable(R.drawable.i), getResources().getDrawable(R.drawable.oh), getResources().getDrawable(R.drawable.l), getResources().getDrawable(R.drawable.jolly), getResources().getDrawable(R.drawable.mpb)};
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (cyclesLeft == 0) {
                    int nextImage = random.nextInt(7);
                    cyclesLeft = random.nextInt(3) + 4;
                    ((ImageView) findViewById(R.id.imageViewMouth)).setImageDrawable(mouthShapes[nextImage]);
                }

                cyclesLeft--;

                handler.postDelayed(this, 1000 / Utility.getFPS());

            }
        }, 1000 / Utility.getFPS());
    }


    private void southParkTest() {
        setContentView(R.layout.activity_quiz_characters);
        View character = findViewById(R.id.imageViewCharacter);
        View mouth = findViewById(R.id.imageViewMouth);

        int characterWidth = Utility.getWidth(this) / 3;
        int characterHeight = characterWidth * 622 / 400;
        int mouthWidth = (int) (characterWidth * 0.275f);
        int mouthHeight = mouthWidth * 261 / 611;

        int mouthTopMargin = characterHeight * 365 / 622;
        int mouthLeftMargin = characterWidth * 160 / 400;

        int layoutTopMargin = ((Utility.getHeight(this) / 2) - characterHeight) * 2 / 3;

        character.getLayoutParams().width = characterWidth;
        character.getLayoutParams().height = characterHeight;
        mouth.getLayoutParams().width = mouthWidth;
        mouth.getLayoutParams().height = mouthHeight;

        Utility.setMargins(mouth, mouthLeftMargin, mouthTopMargin, 0, 0);
        Utility.setMargins(findViewById(R.id.body), 0, layoutTopMargin, 0, 0);
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
        bundle.putInt("packageIndex", Utility.VIP);
        mIntent.putExtras(bundle);

        startActivity(mIntent);
    }


}
