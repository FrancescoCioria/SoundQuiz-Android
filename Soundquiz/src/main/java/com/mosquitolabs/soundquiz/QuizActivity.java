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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
    private boolean stopShaking = false;

    private long lastExecutedTime;

    private ImageView hints;

    private boolean stopAnimation = false;
    private Runnable handlerAnimationTask;

    private QuizData quizData;
    private AudioPlayer audioPlayer = AudioPlayer.getIstance();
    private StringVisualizerView visualizer;
    private GameView gameView;
    private ImageView following;
    private ImageView previous;
    private ImageView play;

    private RelativeLayout revealOneLetterLayout;
    private Button cancel;

    private boolean refreshVisualizer = true;
    private boolean refreshGameView = true;
    private boolean shakeHints = true;
    private boolean refreshViews = true;


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

        startLoop();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }


    private void init() {
//        binding
        hints = (ImageView) findViewById(R.id.hints);
        visualizer = (StringVisualizerView) findViewById(R.id.visualizerView);
        gameView = (GameView) findViewById(R.id.gameView);
        following = (ImageView) findViewById(R.id.following);
        previous = (ImageView) findViewById(R.id.previous);
        play = (ImageView) findViewById(R.id.buttonPlay);
        revealOneLetterLayout = (RelativeLayout) findViewById(R.id.revealOneLetter);
        cancel = (Button) findViewById(R.id.buttonCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToGameMode();
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.followingLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFollowingQuiz();
            }
        });

        findViewById(R.id.previousLayout).setOnClickListener(new View.OnClickListener() {
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
            shakeHints = true;
        }
        refreshGameView = true;
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

    private void startLoop() {
        lastExecutedTime = System.currentTimeMillis();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshGameView) {
                    gameView.refresh();
                }

                if (refreshVisualizer) {
                    visualizer.refresh();
                }

//                if (shakeHints && System.currentTimeMillis() - lastExecutedTime >= 5000) {
//                    startShakeAnimation();
//                    lastExecutedTime = System.currentTimeMillis();
//                }

                handler.postDelayed(this, 1000 / Utility.getFPS());

            }
        }, 1000 / Utility.getFPS());
    }


    private void initSound(final boolean start) {

        int res = getResources().getIdentifier(quizData.getQuizID(), "raw", getPackageName());

        audioPlayer.player = MediaPlayer.create(this, res);

        audioPlayer.resetEqualizer();
        audioPlayer.player.setLooping(false);

        AudioPlayer.getIstance().player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
        int hintsSize = Utility.getWidth(this) * 55 / 720;
        int backWidth = Utility.getWidth(this) * 90 / 720;
        int backHeight = Utility.getWidth(this) * 40 / 720;
        int hintsMargin = hintsSize / 3;

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
        hints.getLayoutParams().width = hints.getLayoutParams().height = hintsSize;
        hints.getLayoutParams().width = hints.getLayoutParams().height = hintsSize;
        findViewById(R.id.back).getLayoutParams().width = backWidth;
        findViewById(R.id.back).getLayoutParams().height = backHeight;
        findViewById(R.id.header).getLayoutParams().height = hintsSize + 2 * hintsMargin;

        Utility.setMargins(imageLayout, leftMarginScreen, topMarginScreen, 0, 0);
        Utility.setMargins(visualizer, leftMarginScreen, 0, 0, 0);

        Utility.setMargins(previous, emptySpace / 3, 0, 0, 0);
        Utility.setMargins(following, 0, 0, emptySpace / 3, 0);
        Utility.setMargins(findViewById(R.id.previousLayout), 0, 543 * 65 / 300, 0, 543 * 65 / 300);
        Utility.setMargins(findViewById(R.id.followingLayout), 0, 543 * 65 / 300, 0, 543 * 65 / 300);

        Utility.setMargins(play, leftMarginScreen + screenWidth / 3, topMarginScreen + (screenHeight - (screenWidth / 3)) / 2, 0, 0);

        Utility.setMargins(hints, 0, 0, hintsMargin, 0);
        Utility.setMargins(findViewById(R.id.back), hintsMargin, 0, 0, 0);


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
        refreshGameView = true;
//        gameView.startLoop();
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
//        visualizer.stopLoop();
        stopVisualizerAnimation();
        refreshVisualizer = false;
        refreshGameView = false;
        shakeHints = false;
//        gameView.stopLoop();
//        stopShakeLoop();

        if (audioPlayer.player != null) {
            audioPlayer.player.release();
            audioPlayer.player = null;
        }
    }


    private void changeToRevealOneLetterMode() {
        gameView.changeToRevealOneLetterMode();
        Utility.setMargins(revealOneLetterLayout, Utility.convertDpToPixels(this, 16), gameView.getMaxY() + Utility.convertDpToPixels(this, 16), Utility.convertDpToPixels(this, 16), 0);
        revealOneLetterLayout.setVisibility(View.VISIBLE);

    }

    public void changeToGameMode() {
        revealOneLetterLayout.setVisibility(View.GONE);
        gameView.changeToGameMode();
    }

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

    private void startShakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        hints.startAnimation(shake);
    }


    public void invalidateVisualizer() {
//        visualizer.stopLoop();
        refreshVisualizer = false;
        visualizer.setVisibility(View.GONE);
    }

    public void startVisualizer() {
        refreshVisualizer = true;
        visualizer.setVisibility(View.VISIBLE);
//        visualizer.startLoop();
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
//        gameView.stopLoop();
        refreshGameView = false;
        gameView.resetSprites();

        resetImages();
        initGameView();
        initButtons();
        initSound(!quizData.isSolved());

        if (!quizData.isSolved()) {
            startVisualizer();
            shakeHints = true;
        } else {
            shakeHints = false;
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
        dialog.findViewById(R.id.main).getLayoutParams().width = Utility.getWidth(this) * 80 / 100;

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
                dialog.cancel();
                changeToRevealOneLetterMode();
            }
        });

        dialog.show();
    }


    public QuizData getQuizData() {
        return quizData;
    }


}
