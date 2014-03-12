package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mosquitolabs.soundquiz.visualizer.VisualizerView;
import com.mosquitolabs.soundquiz.visualizer.renderer.LineRenderer;


public class QuizActivity extends Activity {

    private String answer;
    private String category;
    private int index;
    private QuizData quizData;
    private QuizCollection quizCollection = QuizCollection.getInstance();
    private MediaPlayer mPlayer;
    private MediaPlayer mSilentPlayer;  /* to avoid tunnel player issue */
    private com.mosquitolabs.soundquiz.visualizer.VisualizerView mVisualizerView;
    private boolean hasBeenReset;
    private boolean isFirstTime = true;

    private EditText answerEditText;
    private Button guessButton;
    private Button hintButton;
    //    private Button listenButton;
    private TextView status;
    private TextView hintTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        answer = getIntent().getExtras().getString("answer");
        category = getIntent().getExtras().getString("category");
        index = getIntent().getExtras().getInt("index");

        setQuizDataByCategoryAndAnswer(category, answer);

        getActionBar().setTitle(quizData.getCategroy() + ": " + Integer.toString(index + 1));
        getActionBar().setDisplayHomeAsUpEnabled(true);


//        binding
        guessButton = (Button) findViewById(R.id.guessButton);
        hintButton = (Button) findViewById(R.id.hintButton);
        answerEditText = (EditText) findViewById(R.id.answerEditText);
        status = (TextView) findViewById(R.id.statusTextView);
        hintTextView = (TextView) findViewById(R.id.hintTextView);

        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = answerEditText.getText().toString();
                if (userAnswer.length() > 0) {
                    if (checkAnswer(userAnswer)) {
                        status.setText(userAnswer + " is CORRECT! :)");
                        quizData.setSolvedStatus(true);
                    } else {
                        status.setText(userAnswer + " is WRONG.. :(");
                    }
                }
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimationButton(v);
//                giveHint();
            }
        });


//        init


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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                ;
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private boolean setQuizDataByCategoryAndAnswer(String category, String answer) {
        for (QuizList quizList : quizCollection.getQuizCollection()) {

            if (quizList.getCategory().equals(category)) {
                for (QuizData quizDataTemp : quizList.getQuizList()) {
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


    private void initSound(boolean start) {
        if (index == 0) {
            mPlayer = MediaPlayer.create(this, R.raw.american_beauty);
        } else {
            mPlayer = MediaPlayer.create(this, R.raw.rocky);
        }
        mPlayer.setLooping(false);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                hasBeenReset = true;
            }
        });
        hasBeenReset = true;
        if (start) {
            listenButtonPressed();
        }
    }

    private void initVisualizer() {
        mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
        mVisualizerView.link(mPlayer);
        mVisualizerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenButtonPressed();
            }
        });
        addLineRenderer();
    }

    private void listenButtonPressed() {
        if (!mPlayer.isPlaying()) {
            try {
                mPlayer.start();
                if (hasBeenReset) {
                    hasBeenReset = false;
                    initVisualizer();
                }
            } catch (Exception e) {
            }
        } else {
            mPlayer.pause();

        }
    }

    private void addLineRenderer() {
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1f);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.argb(88, 0, 128, 255));

        Paint lineFlashPaint = new Paint();
        lineFlashPaint.setStrokeWidth(5f);
        lineFlashPaint.setAntiAlias(true);
        lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
        LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
        mVisualizerView.addRenderer(lineRenderer);
    }

    private void cleanUp() {
        if (mPlayer != null) {
            mVisualizerView.release();
            mPlayer.release();
            mPlayer = null;
        }

        if (mSilentPlayer != null) {
            mSilentPlayer.release();
            mSilentPlayer = null;
        }
    }

    private boolean checkAnswer(String userAnswer) {
//        remove any upper case
        String answer = this.answer.toLowerCase();
        userAnswer = userAnswer.toLowerCase();

        if (answer.substring(0, 4).equals("the ")) {
            answer = answer.substring(4);
        }
        if (userAnswer.substring(0, 4).equals("the ")) {
            userAnswer = userAnswer.substring(4);
        }

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
        hintButton.setTextColor(Color.WHITE);
    }

    private void startAnimationButton(View v) {
        final Animation
                animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

//        v.startAnimation(animRotate);
        v.startAnimation(animAlpha);
        giveHint();
    }

}
