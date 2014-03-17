package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;
import com.mosquitolabs.soundquiz.visualizer.VisualizerView;

public class QuizActivity extends Activity {

    private String answer;
    private String category;
    private int quizIndex;
    private int levelIndex;
    private int packageIndex;
    private int level;
    private boolean hasBeenReset;
    private boolean isFirstTime = true;

    private EditText answerEditText;
    private Button checkButton;
    private Button hintButton;
    private TextView status;
    private TextView hintTextView;

    private QuizData quizData;
    private PackageCollection packageCollection = PackageCollection.getInstance();
    private AudioPlayer audioPlayer = AudioPlayer.getIstance;
    private com.mosquitolabs.soundquiz.visualizer.VisualizerView mVisualizerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        answer = getIntent().getExtras().getString("answer");
        category = getIntent().getExtras().getString("category");
        quizIndex = getIntent().getExtras().getInt("quizIndex");
        levelIndex = getIntent().getExtras().getInt("levelIndex");
        packageIndex = getIntent().getExtras().getInt("packageIndex");

        setQuizDataByCategoryAndAnswer(category, answer);

        getActionBar().setTitle(Integer.toString(quizIndex + 1) + "/" + Integer.toString(packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().size()));
        getActionBar().setDisplayHomeAsUpEnabled(true);

//        binding
        checkButton = (Button) findViewById(R.id.checkButton);
        hintButton = (Button) findViewById(R.id.hintButton);
        answerEditText = (EditText) findViewById(R.id.answerEditText);
        status = (TextView) findViewById(R.id.statusTextView);
        hintTextView = (TextView) findViewById(R.id.hintTextView);

//        answerEditText.setBootstrapEditTextEnabled(true);
        answerEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        answerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkButton.performClick();
                    return true;
                }
                return false;
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = answerEditText.getText().toString();
                if (userAnswer.length() > 0) {
                    if (checkAnswer(userAnswer)) {
                        status.setText(userAnswer + " is CORRECT! :)");
                        quizData.setSolvedStatus(true);
//                        answerEditText.setSuccess();
                    } else {
                        status.setText(userAnswer + " is WRONG.. :(");
//                        answerEditText.setDanger();
                    }
                }
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimationButton(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSound(isFirstTime);
        isFirstTime = false;
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
        super.onBackPressed();
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

    private boolean setQuizDataByCategoryAndAnswer(String category, String answer) {
        for (PackageData pack : packageCollection.getPackageCollection()) {
            if (pack.getCategory().equals(category)) {
                for (QuizData quizDataTemp : pack.getLevelList().get(level).getQuizList()) {
                    if (quizDataTemp.getAnswer().equals(answer)) {
                        quizData = quizDataTemp;
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private void initSound(final boolean start) {
        cleanUp();

        switch (quizIndex) {
            case 0:
                audioPlayer.player = MediaPlayer.create(this, R.raw.fox);
                break;
            case 1:
                audioPlayer.player = MediaPlayer.create(this, R.raw.columbia);
                break;
            case 2:
                audioPlayer.player = MediaPlayer.create(this, R.raw.lucas_film);
                break;
            case 3:
                audioPlayer.player = MediaPlayer.create(this, R.raw.warner_bros);
                break;
            case 4:
                audioPlayer.player = MediaPlayer.create(this, R.raw.universal);
                break;
            case 5:
                audioPlayer.player = MediaPlayer.create(this, R.raw.mgm);
                break;
            case 6:
                audioPlayer.player = MediaPlayer.create(this, R.raw.tristar);
                break;
            case 7:
                audioPlayer.player = MediaPlayer.create(this, R.raw.fandango);
                break;
            case 8:
                audioPlayer.player = MediaPlayer.create(this, R.raw.new_line_cinema);
                break;
            case 9:
                audioPlayer.player = MediaPlayer.create(this, R.raw.castle_rock);
                break;
            case 10:
                audioPlayer.player = MediaPlayer.create(this, R.raw.lionsgate);
                break;
            case 11:
                audioPlayer.player = MediaPlayer.create(this, R.raw.walt_disney);
                break;
            default:
                audioPlayer.player = MediaPlayer.create(this, R.raw.fox);
                break;
        }

//        if (index == 0) {
//            mPlayer = MediaPlayer.create(this, R.raw.american_beauty);
//        } else {
//            mPlayer = MediaPlayer.create(this, R.raw.rocky);
//        }
        audioPlayer.resetEqualizer();


        audioPlayer.player.setLooping(false);


        hasBeenReset = true;

        if (start) {
            togglePlay();
        }
        initVisualizer();

    }

    private void initVisualizer() {
        if (mVisualizerView != null) {
            mVisualizerView.disableVisualizer();
            mVisualizerView.release();
            mVisualizerView.invalidate();
        }

        mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
        mVisualizerView.link();
        mVisualizerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });
        mVisualizerView.initBarRenderer();
    }

    private void togglePlay() {
        if (!audioPlayer.player.isPlaying()) {
            audioPlayer.player.start();
        } else {
            audioPlayer.player.pause();
        }
    }

    private void cleanUp() {
        if (mVisualizerView != null) {
            mVisualizerView.disableVisualizer();
            mVisualizerView.release();
            mVisualizerView.invalidate();
            mVisualizerView = null;
        }

        if (audioPlayer.player != null) {
            audioPlayer.player.release();
            audioPlayer.player = null;
        }
    }

    private boolean checkAnswer(String userAnswer) {
//        remove any upper case
        String answer = this.answer.toLowerCase();
        userAnswer = userAnswer.toLowerCase();

//        remove spaces at the end
        while (userAnswer.charAt(userAnswer.length() - 1) == ' ') {
            userAnswer = userAnswer.substring(0, userAnswer.length() - 1);
        }

//        remove "The"
        if (answer.length() > 4 && answer.substring(0, 4).equals("the ")) {
            answer = answer.substring(4);
        }
        if (userAnswer.length() > 4 && userAnswer.substring(0, 4).equals("the ")) {
            userAnswer = userAnswer.substring(4);
        }

//        compare
        if (userAnswer.equals(answer)) {
            return true;
        }

        return false;

    }

    private void giveHint() {
        String hint = new String();
        String[] separated = answer.toUpperCase().split(" ");
        for (String word : separated) {
            for (int i = 0; i < word.length(); i++) {
//                show first letter and last (if word longer then 4)
                if (i == 0) {
                    hint += word.charAt(i);
                    hint += " ";
                } else if (word.length() > 4 && i == word.length() - 1) {
                    hint += word.charAt(i);
                } else {
                    hint += "_ ";
                }
            }
            hint += "  ";

        }
        hintButton.setText(hint);
    }

    private void startAnimationButton(View v) {
        final Animation
                animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        v.startAnimation(animAlpha);
        giveHint();
    }

}
