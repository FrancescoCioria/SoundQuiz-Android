package com.mosquitolabs.soundquiz;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;
import com.mosquitolabs.soundquiz.visualizer.StringVisualizerView;

public class QuizActivity extends Activity {
    private final static int ANIMATION_TIME = 300; // (millis)

    // FFB55A18

    private int quizIndex;
    private int levelIndex;
    private int packageIndex;
    private boolean isFirstTime = true;

    private ImageView hints;

    private boolean stopAnimation = false;
    private Runnable handlerAnimationTask;

    private QuizData quizData;
    private AudioPlayer audioPlayer = AudioPlayer.getIstance;
    private StringVisualizerView visualizer;
    private GameView gameView;
    private ImageView following;
    private ImageView previous;
    private ImageView play;


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

        init();
        initButtons();
        initGameView();
        initVisualizer();

    }

    private void init() {
//        binding
        hints = (ImageView) findViewById(R.id.hints);
        visualizer = (StringVisualizerView) findViewById(R.id.visualizerView);
        gameView = (GameView) findViewById(R.id.gameView);
        following = (ImageView) findViewById(R.id.following);
        previous = (ImageView) findViewById(R.id.previous);
        play = (ImageView) findViewById(R.id.buttonPlay);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        findViewById(R.id.back).setAlpha(0.5f);
//        findViewById(R.id.back).setVisibility(View.INVISIBLE);

//        checkButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String userAnswer = answerEditText.getText().toString();
//                if (userAnswer.length() > 0) {
//                    if (checkAnswer(userAnswer)) {
//                        quizData.setSolved();
//                        PackageCollection.getInstance().modifyQuizInSavedData(quizData);
//                        Intent mIntent = new Intent(QuizActivity.this, WinActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("quizIndex", quizIndex);
//                        bundle.putInt("levelIndex", levelIndex);
//                        bundle.putInt("packageIndex", packageIndex);
//                        mIntent.putExtras(bundle);
//                        QuizActivity.this.startActivity(mIntent);
//                    } else {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
//                        builder.setMessage(userAnswer + " is WRONG.. :(");
//                        builder.setCancelable(false);
//                        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.create().show();
//                    }
//                }
//            }
//        });
//
//        hintButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                quizData.setUsedHint();
//                PackageCollection.getInstance().modifyQuizInSavedData(quizData);
//                AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
//                builder.setMessage(getHint());
//                builder.setCancelable(false);
//                builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.create().show();
//            }
//        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFollowingQuiz();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPreviousQuiz();
            }
        });

        findViewById(R.id.backToMenuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        visualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });


        hints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHintDialog();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanUp();
        initSound(isFirstTime && !quizData.isSolved());
        if (!quizData.isSolved()) {
            startVisualizer();
        }
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

        int res = getResources().getIdentifier(quizData.getQuizID(), "raw", getPackageName());

        audioPlayer.player = MediaPlayer.create(this, res);

        audioPlayer.resetEqualizer();
        audioPlayer.player.setLooping(false);

        AudioPlayer.getIstance.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopVisualizerAnimation();
            }
        });

        if (start) {
            togglePlay();
        }

    }


    private void initVisualizer() {
        visualizer.setVisibility(View.VISIBLE);
        int layoutWidth = (int) (Utility.getWidth(this) * 0.65f);
        int screenWidth = layoutWidth * 401 / 543;
        int screenHeight = layoutWidth * 310 / 543;
        int emptySpace = (Utility.getWidth(this) - layoutWidth) / 2;
        int leftMarginScreen = (int) (51.5f / 543 * layoutWidth);
        int topMarginScreen = (int) (90f / 545 * layoutWidth);

        findViewById(R.id.body).getLayoutParams().width = layoutWidth;

        RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.layoutImageQuiz);

        imageLayout.getLayoutParams().width = screenWidth;
        imageLayout.getLayoutParams().height = screenHeight;
        visualizer.getLayoutParams().height = (int) (screenHeight * 0.45f);
        visualizer.getLayoutParams().width = screenWidth;
        play.getLayoutParams().width = screenWidth / 3;
        play.getLayoutParams().height = screenWidth / 3;
        following.getLayoutParams().width = emptySpace / 3;
        previous.getLayoutParams().width = emptySpace / 3;

        Utility.setMargins(imageLayout, leftMarginScreen, topMarginScreen, 0, 0);
        Utility.setMargins(visualizer, leftMarginScreen, 0, 0, 0);
        Utility.setMargins(previous, emptySpace / 3, 0, 0, 0);
        Utility.setMargins(following, 0, 0, emptySpace / 3, 0);
        Utility.setMargins(play, leftMarginScreen + screenWidth / 3, topMarginScreen + (screenHeight - (screenWidth / 3)) / 2, 0, 0);

//        following.setAlpha(0.4f);
//        previous.setAlpha(0.4f);

        visualizer.setColor(Color.WHITE);
        if (Utility.getWidth(this) >= 720) {
            visualizer.setStroke(4, 2);
        }
    }

    private void startVisualizerAnimation() {
        visualizer.startAnimation();
        play.setVisibility(View.GONE);
    }

    private void stopVisualizerAnimation() {
        visualizer.stopAnimation();
        if (!quizData.isSolved()) {
            play.setVisibility(View.VISIBLE);
        }
    }

    private void initGameView() {
        gameView.init(quizData);
        gameView.startLoop();
    }

    private void togglePlay() {
        if (!audioPlayer.player.isPlaying()) {
            audioPlayer.player.start();
            startVisualizerAnimation();
        } else {
            audioPlayer.player.pause();
            stopVisualizerAnimation();
        }
    }

    private void cleanUp() {
        visualizer.stopLoop();
        stopVisualizerAnimation();

        if (audioPlayer.player != null) {
            audioPlayer.player.release();
            audioPlayer.player = null;
        }
    }

//    private boolean checkAnswer(String userAnswer) {
//        for (String answer : quizData.getAnswers()) {
////        remove any upper case
//            String answerTemp = answer.toLowerCase();
//            userAnswer = userAnswer.toLowerCase();
//
////        remove spaces at the beginning
//            while (userAnswer.charAt(0) == ' ') {
//                userAnswer = userAnswer.substring(1);
//            }
////        remove spaces at the end
//            while (userAnswer.charAt(userAnswer.length() - 1) == ' ') {
//                userAnswer = userAnswer.substring(0, userAnswer.length() - 1);
//            }
//
////        remove "The"
//            if (answerTemp.length() > 4 && answerTemp.substring(0, 4).equals("the ")) {
//                answerTemp = answerTemp.substring(4);
//            }
//            if (userAnswer.length() > 4 && userAnswer.substring(0, 4).equals("the ")) {
//                userAnswer = userAnswer.substring(4);
//            }
//
////        compare
//            if (userAnswer.equals(answerTemp)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private String getHint() {
//        String hint = new String();
//        String[] separated = quizData.getAnswers().get(0).toUpperCase().split(" ");
//        for (String word : separated) {
//            for (int i = 0; i < word.length(); i++) {
////                show first letter and last (if word longer then 4)
//                if (i == 0) {
//                    hint += word.charAt(i);
//                    hint += " ";
//                } else if (word.length() > 4 && i == word.length() - 1) {
//                    hint += word.charAt(i);
//                } else {
//                    hint += "_ ";
//                }
//            }
//            hint += "  ";
//
//        }
//        return hint;
//    }
//
//    public void startWinActivity() {
//        quizData.setSolved();
//        PackageCollection.getInstance().modifyQuizInSavedData(quizData);
//        Intent mIntent = new Intent(QuizActivity.this, WinActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt("quizIndex", quizIndex);
//        bundle.putInt("levelIndex", levelIndex);
//        bundle.putInt("packageIndex", packageIndex);
//        bundle.putInt("spaceFullSize", gameView.getSpaceFullSize());
//        mIntent.putExtras(bundle);
//        QuizActivity.this.startActivity(mIntent);
//    }

    public void initWinPage() {
        getQuizData().setSolved();
        PackageCollection.getInstance().modifyQuizInSavedData(getQuizData());
        initWinGraphics();
        startGrowthAnimation();
    }

    public void initWinGraphics() {
        RelativeLayout winLayout = (RelativeLayout) findViewById(R.id.win);
        Utility.setMargins(winLayout, 0, gameView.getMaxY(), 0, 0);
        winLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.layoutImageQuiz).setVisibility(View.VISIBLE);
        findViewById(R.id.imageQuiz).setVisibility(View.VISIBLE);
        int res = getResources().getIdentifier(getQuizData().getQuizID(), "drawable", getPackageName());
        try {
            ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(res));
        } catch (Exception e) {
            ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(R.drawable.twenty_century_fox));
        }
        ((ImageView) findViewById(R.id.imageViewTV)).setImageDrawable(getResources().getDrawable(R.drawable.tv_simpsons_empty_with_shadow));
        invalidateVisualizer();
        play.setVisibility(View.GONE);
        hints.setVisibility(View.GONE);
    }

    @TargetApi(14)
    private void startGrowthAnimation() {
        stopAnimation = false;
        findViewById(R.id.imageQuiz).setScaleX(0);
        findViewById(R.id.imageQuiz).setScaleY(0);
        findViewById(R.id.win).setScaleX(0);
        findViewById(R.id.win).setScaleY(0);
        final Handler handler = new Handler();
        final float deltaScale = 1.0f / (ANIMATION_TIME / (1000 / Utility.getFPS()));
        handlerAnimationTask = new Runnable() {
            @Override
            public void run() {
                float scale = findViewById(R.id.imageQuiz).getScaleX() + deltaScale;
                if (scale > 1.0f) {
                    scale = 1.0f;
                    stopAnimation = true;
                }
                findViewById(R.id.imageQuiz).setScaleX(scale);
                findViewById(R.id.imageQuiz).setScaleY(scale);
                findViewById(R.id.win).setScaleX(scale);
                findViewById(R.id.win).setScaleY(scale);
                if (!stopAnimation) {
                    handler.postDelayed(handlerAnimationTask, 1000 / Utility.getFPS());
                }
            }
        };
        handlerAnimationTask.run();
    }


    public void invalidateVisualizer() {
        visualizer.stopLoop();
        visualizer.setVisibility(View.GONE);
    }

    public void startVisualizer() {
        visualizer.setVisibility(View.VISIBLE);
        visualizer.startLoop();
    }


    private void resetImages() {
        findViewById(R.id.layoutImageQuiz).setVisibility(View.GONE);
        findViewById(R.id.win).setVisibility(View.GONE);
        hints.setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.imageViewTV)).setImageDrawable(getResources().getDrawable(R.drawable.tv_simpsons_with_shadow));
    }


    private void reset() {
        audioPlayer.player.release();
        visualizer.stopAnimation();
        quizData = PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(quizIndex);
        gameView.stopLoop();
        gameView.resetSprites();

        resetImages();
        initGameView();
        initButtons();
        initSound(!quizData.isSolved());

        if (!quizData.isSolved()) {
            startVisualizer();
        }

    }

    private void goToFollowingQuiz() {
        if (quizIndex < 14) {
            quizIndex++;
            reset();
        }
    }

    private void goToPreviousQuiz() {
        if (quizIndex > 0) {
            quizIndex--;
            reset();
        }
    }


    private void initButtons() {
        if (quizIndex == 14) {
            findViewById(R.id.following).setVisibility(View.GONE);
        } else if (quizIndex == 0) {
            findViewById(R.id.previous).setVisibility(View.GONE);
        } else {
            findViewById(R.id.previous).setVisibility(View.VISIBLE);
            findViewById(R.id.following).setVisibility(View.VISIBLE);
        }

    }


    private void openHintDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_hints);
        dialog.findViewById(R.id.main).getLayoutParams().width = Utility.getWidth(this) * 65 / 100;

        dialog.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.findViewById(R.id.removeWrongLetters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                gameView.removeWrongLetters();
            }
        });

        dialog.findViewById(R.id.revealFirstLetters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                gameView.revealFirstLetters();
            }
        });

        dialog.findViewById(R.id.revealOneLetter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }


    public QuizData getQuizData() {
        return quizData;
    }


}
