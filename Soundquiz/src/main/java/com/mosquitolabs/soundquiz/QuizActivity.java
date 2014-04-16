package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;
import com.mosquitolabs.soundquiz.visualizer.GuitarStringsVisualizerView;
import com.mosquitolabs.soundquiz.visualizer.StringVisualizerView;

public class QuizActivity extends Activity {
    private final static int ANIMATION_TIME = 300; // (millis)

    private final static int CINEMA = 0;
    private final static int MUSIC = 1;
    private final static int VIP = 2;

    // FFB55A18

    private int quizIndex;
    private int levelIndex;
    private int packageIndex;
    private int fireworkCounter = 0;
    private boolean isFirstTime = true;
    private boolean stopShaking = false;
    private boolean fireworkAnimation = false;

    private long lastExecutedTime;

    private ImageView hints;

    private boolean stopAnimation = false;
    private Runnable handlerAnimationTask;

    private QuizData quizData;
    private AudioPlayer audioPlayer = AudioPlayer.getIstance();
    private StringVisualizerView visualizerCinema;
    private GuitarStringsVisualizerView visualizerMusic;
    private GameView gameView;
    private ImageView following;
    private ImageView previous;
    private ImageView play;
    private TextView type;

    private RelativeLayout revealOneLetterLayout;
    private Button cancel;

    private boolean refreshVisualizer = true;
    private boolean refreshGameView = true;
    private boolean shakeHints = true;
    private boolean refreshViews = true;

    private View[] fireworkViews = {null, null, null, null};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);

        quizIndex = getIntent().getExtras().getInt("quizIndex");
        levelIndex = getIntent().getExtras().getInt("levelIndex");
        packageIndex = getIntent().getExtras().getInt("packageIndex");

        quizData = PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(quizIndex);

        init();
        initButtons();
        initGameView();
        initViews();

        startLoop();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }


    private void init() {
        Log.d("INIT", " inside");

        switch (packageIndex) {
            case CINEMA:
                setContentView(R.layout.activity_quiz_cinema);
                visualizerCinema = (StringVisualizerView) findViewById(R.id.visualizerView);
                visualizerCinema.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        togglePlay();
                    }
                });
                type = (TextView) findViewById(R.id.type);
                if (quizData.isSolved()) {
                    type.setVisibility(View.GONE);
                }
                type.setText(quizData.getType());
                findViewById(R.id.imageQuiz).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFireworkAnimation();
                    }
                });
                break;
            case MUSIC:
                setContentView(R.layout.activity_quiz_music);
                visualizerMusic = (GuitarStringsVisualizerView) findViewById(R.id.visualizerView);
                visualizerMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        togglePlay();
                    }
                });
                break;
            case VIP:
                setContentView(R.layout.activity_quiz_cinema);
                break;
        }

//      general  binding
        hints = (ImageView) findViewById(R.id.hints);
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


        fireworkViews[0] = findViewById(R.id.fireworkLeftTop);
        fireworkViews[1] = findViewById(R.id.fireworkRightTop);
        fireworkViews[2] = findViewById(R.id.fireworkLeftBottom);
        fireworkViews[3] = findViewById(R.id.fireworkRightBottom);


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
        switch (packageIndex) {
            case CINEMA:
                setVisualizerVisible(!quizData.isSolved());
                break;
            case MUSIC:
                setVisualizerVisible(true);
                break;
            case VIP:

                break;
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

    private void refreshVisualizer() {
        if (!refreshVisualizer) {
            return;
        }

        switch (packageIndex) {
            case CINEMA:
                visualizerCinema.refresh();
                break;
            case MUSIC:
                visualizerMusic.refresh();
                break;
            case VIP:

                break;
        }
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

                refreshVisualizer();

                if (fireworkAnimation && fireworkCounter < fireworkViews.length) {
                    startSingleFireworkAnimation(fireworkViews[fireworkCounter]);
                    fireworkCounter++;
                }

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

    private void initViews() {
        //general
        int layoutWidth = (int) (Utility.getWidth(this) * 0.65f);
        int emptySpace = (Utility.getWidth(this) - layoutWidth) / 2;
        int hintsSize = Utility.getWidth(this) * 58 / 720;
        int backWidth = Utility.getWidth(this) * 97 / 720;
        int backHeight = Utility.getWidth(this) * 42 / 720;
        int hintsMargin = hintsSize / 3;
        int headerHeight = hintsSize + 2 * hintsMargin;
        int fireworkSize = layoutWidth / 3;
        int topMarginFireworksTop = (layoutWidth * 50 / 543 + headerHeight) - fireworkSize / 2;
        int topMarginFireworksBottom = (layoutWidth * 50 / 543 + headerHeight);
        int lateralMarginFireworksTop = emptySpace - fireworkSize / 2;
        int lateralMarginFireworksBottom = emptySpace - fireworkSize * 3 / 4;

        following.getLayoutParams().width = emptySpace / 3;
        previous.getLayoutParams().width = emptySpace / 3;
        hints.getLayoutParams().width = hints.getLayoutParams().height = hintsSize;
        hints.getLayoutParams().width = hints.getLayoutParams().height = hintsSize;
        findViewById(R.id.back).getLayoutParams().width = backWidth;
        findViewById(R.id.back).getLayoutParams().height = backHeight;
        findViewById(R.id.header).getLayoutParams().height = headerHeight;
        findViewById(R.id.fireworkLeftTop).getLayoutParams().width = findViewById(R.id.fireworkLeftTop).getLayoutParams().height = findViewById(R.id.fireworkRightTop).getLayoutParams().width = findViewById(R.id.fireworkRightTop).getLayoutParams().height = fireworkSize;
        findViewById(R.id.fireworkLeftBottom).getLayoutParams().width = findViewById(R.id.fireworkLeftBottom).getLayoutParams().height = findViewById(R.id.fireworkRightBottom).getLayoutParams().width = findViewById(R.id.fireworkRightBottom).getLayoutParams().height = fireworkSize;

        Utility.setMargins(previous, emptySpace / 3, 0, 0, 0);
        Utility.setMargins(following, 0, 0, emptySpace / 3, 0);
        Utility.setMargins(findViewById(R.id.previousLayout), 0, 543 * 65 / 300, 0, 543 * 65 / 300);
        Utility.setMargins(findViewById(R.id.followingLayout), 0, 543 * 65 / 300, 0, 543 * 65 / 300);

        Utility.setMargins(hints, 0, 0, hintsMargin, 0);
        Utility.setMargins(findViewById(R.id.back), hintsMargin, 0, 0, 0);


        Utility.setMargins(findViewById(R.id.fireworkLeftTop), lateralMarginFireworksTop, topMarginFireworksTop, 0, 0);
        Utility.setMargins(findViewById(R.id.fireworkLeftBottom), lateralMarginFireworksBottom, topMarginFireworksBottom, 0, 0);
        Utility.setMargins(findViewById(R.id.fireworkRightTop), 0, topMarginFireworksTop, lateralMarginFireworksTop, 0);
        Utility.setMargins(findViewById(R.id.fireworkRightBottom), 0, topMarginFireworksBottom, lateralMarginFireworksBottom, 0);


        switch (packageIndex) {
            case CINEMA:
                initCinemaViews();
                break;
            case MUSIC:
                initMusicViews();
                break;
            case VIP:
                break;
        }


    }


    private void initCinemaViews() {
        visualizerCinema.setVisibility(View.VISIBLE);
        int layoutWidth = (int) (Utility.getWidth(this) * 0.65f);
        int screenWidth = layoutWidth * 401 / 543;
        int screenHeight = layoutWidth * 310 / 543;
        int leftMarginScreen = (int) (51.5f / 543 * layoutWidth);
        int topMarginScreen = (int) (90f / 545 * layoutWidth);

        findViewById(R.id.body).getLayoutParams().width = layoutWidth;

        RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.layoutImageQuiz);

        imageLayout.getLayoutParams().width = screenWidth;
        imageLayout.getLayoutParams().height = screenHeight;
        visualizerCinema.getLayoutParams().height = (int) (screenHeight * 0.45f);
        visualizerCinema.getLayoutParams().width = screenWidth;
        play.getLayoutParams().width = screenWidth / 3;
        play.getLayoutParams().height = screenWidth / 3;

        type.getLayoutParams().width = screenWidth;
        type.getLayoutParams().height = screenHeight / 3;

        Utility.setMargins(imageLayout, leftMarginScreen, topMarginScreen, 0, 0);
        Utility.setMargins(visualizerCinema, leftMarginScreen, 0, 0, 0);
        Utility.setMargins(type, leftMarginScreen, topMarginScreen, 0, 0);

        Utility.setMargins(play, leftMarginScreen + screenWidth / 3, topMarginScreen + (screenHeight - (screenWidth / 3)) / 2, 0, 0);

        type.setTextSize(Utility.pixelsToSp(this, screenHeight / 9));

        visualizerCinema.setColor(Color.WHITE);
        if (Utility.getWidth(this) >= 720) {
            visualizerCinema.setStroke(4, 2);
        }
    }

    private void initMusicViews() {
        int guitarWidth = Utility.getHeight(this) * 498 / 1356;
        int guitarHeight = Utility.getHeight(this) / 2;

        View guitar = findViewById(R.id.imageViewGuitar);
        guitar.getLayoutParams().height = guitarHeight;
        guitar.getLayoutParams().width = guitarWidth;

        int center = (Utility.getWidth(this) - guitarWidth) / 2 + Utility.getHeight(this) * 47 / 1356;
//        int stringBaseWidth = Utility.getHeight(this) * 191 / 1356;
        int stringTopWidth = Utility.getHeight(this) * 165 / 1356;

        float compressionFactor = 130f / 160f;

        visualizerMusic.getLayoutParams().width = (int) (stringTopWidth / 0.9f);
        visualizerMusic.getLayoutParams().height = guitarHeight * 605 / 678;
        visualizerMusic.setStroke(Utility.getHeight(this) * 6 / 1356);
        visualizerMusic.setCompressionFactor(compressionFactor);
        visualizerMusic.setColor(Color.rgb(248, 246, 236));

        play.getLayoutParams().width = guitarWidth * 201 / 498;
        play.getLayoutParams().height = guitarWidth * 201 / 498;

        findViewById(R.id.imageQuiz).getLayoutParams().width = guitarWidth * 310 / 498;
        findViewById(R.id.imageQuiz).getLayoutParams().height = guitarWidth * 310 / 498;

        Utility.setMargins(guitar, center, 0, 0, 0);
        Utility.setMargins(play, guitarHeight * 97 / 678, guitarHeight * 104 / 678, 0, 0);
    }

    private void initGameView() {
        gameView.init(quizData);
        refreshGameView = true;
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


    public void initWinPage() {
        getQuizData().setSolved();
        PackageCollection.getInstance().modifyQuizInSavedData(getQuizData());

        initWinGraphics();
        startWinAnimation();
    }

    public void initWinGraphics() {
        Log.d("WIN GRAPHICS", " inside");
        RelativeLayout winLayout = (RelativeLayout) findViewById(R.id.win);
        Utility.setMargins(winLayout, 0, gameView.getMaxY(), 0, 0);
        winLayout.setVisibility(View.VISIBLE);

        play.setVisibility(View.GONE);
        hints.setVisibility(View.GONE);

        switch (packageIndex) {
            case CINEMA:
                initCinemaWinGraphics();
                break;
            case MUSIC:
                initMusicWinGraphics();
                break;
            case VIP:
                break;
        }

        Log.d("WIN GRAPHICS", " win " + findViewById(R.id.win).getVisibility());

    }

    private void initCinemaWinGraphics() {
        setVisualizerVisible(false);
        findViewById(R.id.layoutImageQuiz).setVisibility(View.VISIBLE);
        findViewById(R.id.imageQuiz).setVisibility(View.VISIBLE);
        type.setVisibility(View.GONE);
        int res = getResources().getIdentifier(getQuizData().getQuizID(), "drawable", getPackageName());
        try {
            ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(res));
        } catch (Exception e) {
            ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(R.drawable.twenty_century_fox));
        }
        ((ImageView) findViewById(R.id.imageViewTV)).setImageDrawable(getResources().getDrawable(R.drawable.tv_simpsons_empty_with_shadow));
    }

    private void initMusicWinGraphics() {
        setVisualizerVisible(true);
        stopVisualizerAnimation();
        findViewById(R.id.layoutImageQuiz).setVisibility(View.VISIBLE);
        findViewById(R.id.imageQuiz).setVisibility(View.VISIBLE);
        int res = getResources().getIdentifier(getQuizData().getQuizID(), "drawable", getPackageName());
        try {
            ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(res));
        } catch (Exception e) {
            ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(R.drawable.twenty_century_fox));
        }
    }

    private void startWinAnimation() {
        Log.d("WIN ANIMATION", " inside");

        TranslateAnimation slideAnimation = new TranslateAnimation(-Utility.getWidth(this) / 2, 0, 0, 0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        slideAnimation.setDuration(300);
        alphaAnimation.setDuration(300);

        switch (packageIndex) {
            case CINEMA:
                findViewById(R.id.imageQuiz).startAnimation(scaleAnimation);
                findViewById(R.id.win).startAnimation(scaleAnimation);
                break;
            case MUSIC:
                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(slideAnimation);
                animationSet.setDuration(300);
                findViewById(R.id.imageQuiz).startAnimation(animationSet);
                findViewById(R.id.win).startAnimation(scaleAnimation);

                break;
            case VIP:
                break;
        }
        startFireworkAnimation();
    }

    private void startFireworkAnimation() {
        fireworkCounter = 0;
        fireworkAnimation = true;
        AudioPlayer.getIstance().playFireworks();
    }

    private void startSingleFireworkAnimation(final View view) {
        view.setAlpha(1);
        view.setScaleX(1);
        view.setScaleY(1);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);

        scaleAnimation.setDuration(600);
        alphaAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);
        alphaAnimation.setFillAfter(true);


        view.setVisibility(View.VISIBLE);
        Log.d("ANIMATION FIREWORK", " inside");

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(alphaAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(scaleAnimation);
    }

    private void startVisualizerAnimation() {
        switch (packageIndex) {
            case CINEMA:
                visualizerCinema.startAnimation();
                break;
            case MUSIC:
                visualizerMusic.startAnimation();
                break;
            case VIP:
                break;
        }
        play.setVisibility(View.GONE);
    }

    private void stopVisualizerAnimation() {
        switch (packageIndex) {
            case CINEMA:
                visualizerCinema.stopAnimation();
                break;
            case MUSIC:
                visualizerMusic.stopAnimation();
                break;
            case VIP:

                break;
        }
        play.setVisibility(quizData.isSolved() ? View.GONE : View.VISIBLE);
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
        stopVisualizerAnimation();
        refreshVisualizer = false;
        refreshGameView = false;
        shakeHints = false;

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


    private void startShakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        hints.startAnimation(shake);
    }


    public void setVisualizerVisible(boolean isVisible) {
        refreshVisualizer = isVisible;
        switch (packageIndex) {
            case CINEMA:
                visualizerCinema.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                break;
            case MUSIC:
                visualizerMusic.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                break;
            case VIP:

                break;
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

    private void reset() {
        audioPlayer.player.release();
        stopVisualizerAnimation();
        quizData = PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(quizIndex);
        refreshGameView = false;
        gameView.resetSprites();

        resetImages();
        initGameView();
        initButtons();

        initSound(!quizData.isSolved());
    }


    private void resetImages() {
        Log.d("RESET IMAGES", " inside");
        findViewById(R.id.win).setVisibility(View.GONE);
        hints.setVisibility(View.VISIBLE);
        findViewById(R.id.layoutImageQuiz).setVisibility(View.GONE);

        switch (packageIndex) {
            case CINEMA:
                ((ImageView) findViewById(R.id.imageViewTV)).setImageDrawable(getResources().getDrawable(R.drawable.tv_simpsons_with_shadow));
                type.setVisibility(quizData.isSolved() ? View.GONE : View.VISIBLE);
                type.setText(quizData.getType());
                setVisualizerVisible(!quizData.isSolved());
                break;
            case MUSIC:
                setVisualizerVisible(true);
                break;
            case VIP:

                break;
        }
        Log.d("RESET IMAGES", " win " + findViewById(R.id.win).getVisibility());
    }

    private void openHintDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        switch (getPackageIndex()) {
            case CINEMA:
                dialog.setContentView(R.layout.dialog_hints_cinema);
                break;
            case MUSIC:
                dialog.setContentView(R.layout.dialog_hints_music);
                break;
            case VIP:

                break;
        }

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

    public int getPackageIndex() {
        return packageIndex;
    }


}
