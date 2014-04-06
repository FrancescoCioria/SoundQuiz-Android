package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;


public class WinActivity extends Activity {

    private int quizIndex;
    private int levelIndex;
    private int packageIndex;
    private QuizData quizData;
    private AudioPlayer audioPlayer = AudioPlayer.getIstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        Utility.hideActionbar(this);

        quizIndex = getIntent().getExtras().getInt("quizIndex");
        levelIndex = getIntent().getExtras().getInt("levelIndex");
        packageIndex = getIntent().getExtras().getInt("packageIndex");

        quizData = PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(quizIndex);

        Button back = (Button) findViewById(R.id.backButton);
        Button play = (Button) findViewById(R.id.buttonPlay);
        ImageView image = (ImageView)findViewById(R.id.image);
        TextView textViewTitle = (TextView)findViewById(R.id.textViewTitle);
        TextView textViewWiki= (TextView)findViewById(R.id.textViewWiki);

        textViewTitle.setText(quizData.getAnswers().get(0));

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        textViewWiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWikipedia();
            }
        });

        int res = getResources().getIdentifier(quizData.getQuizID(), "drawable", getPackageName());
        try {
            image.setImageDrawable(getResources().getDrawable(res));
        }catch (Exception e){
            image.setImageDrawable(getResources().getDrawable(R.drawable.twenty_century_fox));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanUp();
        initSound();
    }

    @Override
    protected void onPause() {
        cleanUp();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cleanUp();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        cleanUp();
        Intent intent = new Intent(WinActivity.this, LevelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt("levelIndex", levelIndex);
        bundle.putInt("packageIndex", packageIndex);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    private void initSound() {
        int res = getResources().getIdentifier(quizData.getQuizID(), "raw", getPackageName());

        audioPlayer.player = MediaPlayer.create(this, res);

        audioPlayer.resetEqualizer();
        audioPlayer.player.setLooping(false);

        AudioPlayer.getIstance.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

    }

    private void cleanUp() {
        if (audioPlayer.player != null) {
            audioPlayer.player.release();
            audioPlayer.player = null;
        }
    }

    private void togglePlay() {
        if (!audioPlayer.player.isPlaying()) {
            audioPlayer.player.start();
        } else {
            audioPlayer.player.pause();
        }
    }

    private void openWikipedia() {
        if (Utility.isOnline(this)) {
            String url = "http://en.wikipedia.org/wiki/20_century_fox";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } else {
            Utility.shortToast("No internet connection", this);
        }
    }


}
