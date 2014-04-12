package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;
import com.mosquitolabs.soundquiz.visualizer.StringVisualizerView;


public class WinActivity extends Activity {


    private int quizIndex;
    private int levelIndex;
    private int packageIndex;
    private int spaceFullSize;
    private StringVisualizerView visualizer;
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
        spaceFullSize = getIntent().getExtras().getInt("spaceFullSize");

        quizData = PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(quizIndex);

        Button back = (Button) findViewById(R.id.backButton);
        ImageView image = (ImageView) findViewById(R.id.image);
        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        TextView textViewWiki = (TextView) findViewById(R.id.textViewWiki);
        visualizer = (StringVisualizerView) findViewById(R.id.visualizerView);
        AnswerView answerView = (AnswerView) findViewById(R.id.answerView);

        textViewTitle.setText(quizData.getAnswers().get(0));

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

        visualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });

        int res = getResources().getIdentifier(quizData.getQuizID(), "drawable", getPackageName());
        try {
            image.setImageDrawable(getResources().getDrawable(res));
        } catch (Exception e) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.twenty_century_fox));
        }

        initVisualizer();

        int size = spaceFullSize * 90 / 100;
        int GAP = spaceFullSize - size;
        int ROW_GAP = size / 5;

        int marginY = 0;
        for (String row : quizData.getRows()) {
            marginY = Math.max(marginY, (int) ((row.replace(" ", "").length() * size + (row.length() - row.replace(" ", "").length()) * size / 2 + (row.length() - (row.length() - row.replace(" ", "").length()) * 2 - 1) * GAP) * 0.04f));
        }

        answerView.getLayoutParams().height = 2 * marginY + quizData.getRows().size() * size + (quizData.getRows().size() - 1) * ROW_GAP;


        answerView.init(quizData);

    }

    @Override
    protected void onResume() {
        super.onResume();
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
                visualizer.stopAnimation();
            }
        });


        visualizer.startLoop();

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
            visualizer.startAnimation();
        } else {
            audioPlayer.player.pause();
            visualizer.stopAnimation();
        }
    }

    private void initVisualizer() {
        int tvWidth = (int) (Utility.getWidth(this) * 0.65f);
        ImageView image = (ImageView) findViewById(R.id.image);
        findViewById(R.id.imageViewTV).getLayoutParams().width = tvWidth;
        image.getLayoutParams().width = tvWidth * 401 / 543;
        image.getLayoutParams().height = tvWidth * 310 / 543;
        visualizer.getLayoutParams().height = (int) (tvWidth * 310f / 543f * 0.45f);
        visualizer.getLayoutParams().width = tvWidth * 401 / 543;
        int leftMargin = (int) ((Utility.getWidth(this) - tvWidth) / 2 + 51.5f / 543 * tvWidth);
        int topMargin = (int) (94f / 545 * tvWidth);
        Utility.setMargins(visualizer, leftMargin, 0, 0, 0);
        Utility.setMargins(image, leftMargin, topMargin, 0, 0);

        visualizer.setColor(Color.WHITE);
        if (Utility.getWidth(this) >= 720) {
            visualizer.setStroke(4, 2);
        }
    }

    public QuizData getQuizData() {
        return quizData;
    }

    public int getSpaceFullSize() {
        return spaceFullSize;
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
