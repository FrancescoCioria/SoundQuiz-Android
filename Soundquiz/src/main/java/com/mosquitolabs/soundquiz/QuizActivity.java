package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;
import com.mosquitolabs.soundquiz.visualizer.StringVisualizerView;

public class QuizActivity extends Activity {

    private int quizIndex;
    private int levelIndex;
    private int packageIndex;
    private boolean isFirstTime = true;

    private EditText answerEditText;
    private Button checkButton;
    private Button hintButton;
    private ImageView TV;

    private QuizData quizData;
    private AudioPlayer audioPlayer = AudioPlayer.getIstance;
    private StringVisualizerView visualizer;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);

        quizIndex = getIntent().getExtras().getInt("quizIndex");
        levelIndex = getIntent().getExtras().getInt("levelIndex");
        packageIndex = getIntent().getExtras().getInt("packageIndex");

        quizData = PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(quizIndex);

//        binding
        checkButton = (Button) findViewById(R.id.checkButton);
        hintButton = (Button) findViewById(R.id.hintButton);
        answerEditText = (EditText) findViewById(R.id.answerEditText);
        TV = (ImageView) findViewById(R.id.imageViewTV);
        visualizer = (StringVisualizerView) findViewById(R.id.visualizerView);
        gameView = (GameView) findViewById(R.id.gameView);
        TextView back = (TextView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        answerEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        answerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    checkButton.performClick();
//                    return true;
//                }
//                return false;
//            }
//        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = answerEditText.getText().toString();
                if (userAnswer.length() > 0) {
                    if (checkAnswer(userAnswer)) {
                        quizData.setSolved();
                        PackageCollection.getInstance().modifyQuizInSavedData(quizData);
                        Intent mIntent = new Intent(QuizActivity.this, WinActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("quizIndex", quizIndex);
                        bundle.putInt("levelIndex", levelIndex);
                        bundle.putInt("packageIndex", packageIndex);
                        mIntent.putExtras(bundle);
                        QuizActivity.this.startActivity(mIntent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
                        builder.setMessage(userAnswer + " is WRONG.. :(");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizData.setUsedHint();
                PackageCollection.getInstance().modifyQuizInSavedData(quizData);
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
                builder.setMessage(getHint());
                builder.setCancelable(false);
                builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });


        visualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });

        initVisualizer();
//        gameView.init(quizData);
        gameView.startLoop();

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


    private void initSound(final boolean start) {
        cleanUp();
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
        if (start) {
            togglePlay();
        }

    }

    private void initVisualizer() {
        int layoutWidth = (int) (Utility.getWidth(this) * 0.65f);
        findViewById(R.id.body).getLayoutParams().width = layoutWidth;
        visualizer.getLayoutParams().height = Utility.getWidth(this) / 6;
        visualizer.getLayoutParams().width = layoutWidth * 401 / 545;
        int leftMargin = (int) (51.5f / 543 * layoutWidth);
        Utility.setMargins(visualizer, leftMargin, 0, 0, 0);

        visualizer.setColor(Color.WHITE);
        if (Utility.getWidth(this) >= 720) {
            visualizer.setStroke(4, 2);
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

    private void cleanUp() {
        visualizer.stopLoop();
        visualizer.stopAnimation();

        if (audioPlayer.player != null) {
            audioPlayer.player.release();
            audioPlayer.player = null;
        }
    }

    private boolean checkAnswer(String userAnswer) {
        for (String answer : quizData.getAnswers()) {
//        remove any upper case
            String answerTemp = answer.toLowerCase();
            userAnswer = userAnswer.toLowerCase();

//        remove spaces at the beginning
            while (userAnswer.charAt(0) == ' ') {
                userAnswer = userAnswer.substring(1);
            }
//        remove spaces at the end
            while (userAnswer.charAt(userAnswer.length() - 1) == ' ') {
                userAnswer = userAnswer.substring(0, userAnswer.length() - 1);
            }

//        remove "The"
            if (answerTemp.length() > 4 && answerTemp.substring(0, 4).equals("the ")) {
                answerTemp = answerTemp.substring(4);
            }
            if (userAnswer.length() > 4 && userAnswer.substring(0, 4).equals("the ")) {
                userAnswer = userAnswer.substring(4);
            }

//        compare
            if (userAnswer.equals(answerTemp)) {
                return true;
            }
        }

        return false;
    }

    private String getHint() {
        String hint = new String();
        String[] separated = quizData.getAnswers().get(0).toUpperCase().split(" ");
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
        return hint;
    }

    private void startAnimationButton(View v) {
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        v.startAnimation(animAlpha);
        hintButton.setText(getHint());
    }

    public QuizData getQuizData() {
        return quizData;
    }

    public void startWinActivity() {
        quizData.setSolved();
        PackageCollection.getInstance().modifyQuizInSavedData(quizData);
        Intent mIntent = new Intent(QuizActivity.this, WinActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("quizIndex", quizIndex);
        bundle.putInt("levelIndex", levelIndex);
        bundle.putInt("packageIndex", packageIndex);
        mIntent.putExtras(bundle);
        QuizActivity.this.startActivity(mIntent);
    }


}
