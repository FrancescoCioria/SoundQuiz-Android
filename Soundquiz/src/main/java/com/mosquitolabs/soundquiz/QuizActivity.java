package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.mosquitolabs.soundquiz.visualizer.CharacterVisualizerHandler;
import com.mosquitolabs.soundquiz.visualizer.GuitarStringsVisualizerView;
import com.mosquitolabs.soundquiz.visualizer.StringVisualizerView;

public class QuizActivity extends Activity {
    private final static int ANIMATION_TIME = 300; // (millis)


    // FFB55A18

    private int quizIndex;
    private int levelIndex;
    private int packageIndex;
    private int fireworkCounter = 0;
    private boolean isFirstTime = true;
    private boolean stopShaking = false;
    private boolean fireworkAnimation = false;

    private long lastExecutedTime;

    private View hints;
    private View back;

    private boolean stopAnimation = false;
    private Runnable handlerAnimationTask;

    private AudioPlayer audioPlayer = AudioPlayer.getIstance();
    private StringVisualizerView visualizerCinema;
    private GuitarStringsVisualizerView visualizerMusic;
    private CharacterVisualizerHandler visualizerCharacter;
    private GameView gameView;
    private ImageView following;
    private ImageView previous;
    private ImageView play;
    private ImageView playWon;
    //    private ImageView play;
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


        if (getQuizData() == null) {
            Log.e("QUIZ", "quiz is null");
        }

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
            case Utility.CINEMA:
                setContentView(R.layout.activity_quiz_cinema);
                visualizerCinema = (StringVisualizerView) findViewById(R.id.visualizerView);
                visualizerCinema.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        togglePlay();
                    }
                });
                type = (TextView) findViewById(R.id.type);
                if (getQuizData().isSolved()) {
                    type.setVisibility(View.GONE);
                }
                type.setText(getQuizData().getType());
//                findViewById(R.id.imageQuiz).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startFireworkAnimation();
//                    }
//                });
                break;
            case Utility.MUSIC:
                setContentView(R.layout.activity_quiz_music);
                visualizerMusic = (GuitarStringsVisualizerView) findViewById(R.id.visualizerView);
                visualizerMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        togglePlay();
                    }
                });
                break;
            case Utility.VIP:
                setContentView(R.layout.activity_quiz_characters_guess_who);
                visualizerCharacter = new CharacterVisualizerHandler(this);
                break;
        }

//      general  binding
        hints = findViewById(R.id.hintsPressView);
        back = findViewById(R.id.backPressView);
        gameView = (GameView) findViewById(R.id.gameView);
        following = (ImageView) findViewById(R.id.following);
        previous = (ImageView) findViewById(R.id.previous);
        play = (ImageView) findViewById(R.id.buttonPlay);
        playWon = (ImageView) findViewById(R.id.buttonPlayWon);
        revealOneLetterLayout = (RelativeLayout) findViewById(R.id.revealOneLetter);
        cancel = (Button) findViewById(R.id.buttonCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToGameMode();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
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

        playWon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
//                Utility.shortToast("toggleplay", QuizActivity.this);
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
        initSound(isFirstTime && !getQuizData().isSolved());
        switch (packageIndex) {
            case Utility.CINEMA:
                setVisualizerVisible(!getQuizData().isSolved());
                break;
            case Utility.MUSIC:
                setVisualizerVisible(true);
                break;
            case Utility.VIP:
                setVisualizerVisible(true);
                break;
        }
        playWon.setVisibility(getQuizData().isSolved() ? View.VISIBLE : View.GONE);
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
            case Utility.CINEMA:
                visualizerCinema.refresh();
                break;
            case Utility.MUSIC:
                visualizerMusic.refresh();
                break;
            case Utility.VIP:
                visualizerCharacter.refresh();
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
        int res = getResources().getIdentifier(getQuizData().getID(), "raw", getPackageName());

        audioPlayer.createPlayer(this, res);
        audioPlayer.linkOnCompletionListenerToPlayer(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopVisualizerAnimation();
            }
        });

        if (start) {
            togglePlay();
        }

    }

    public void resetPlayer() {
        audioPlayer.resetPlayer(this);

    }

    private void initViews() {
        //general
        int layoutWidth = (int) (Utility.getWidth(this) * 0.65f);
        int emptySpace = (Utility.getWidth(this) - layoutWidth) / 2;
        int hintsSize = Math.max(Utility.getWidth(this) * 58 / 720, Utility.convertDpToPixels(this, 32));
        int backWidth = hintsSize * 106 / 58;
        int backHeight = hintsSize * 46 / 58;
        int hintsMargin = hintsSize / 3;
        int headerHeight = hintsSize + 2 * hintsMargin;
        int fireworkSize = layoutWidth / 3;
        int topMarginFireworksTop = (layoutWidth * 50 / 543 + headerHeight) - fireworkSize / 2;
        int topMarginFireworksBottom = (layoutWidth * 50 / 543 + headerHeight);
        int lateralMarginFireworksTop = emptySpace - fireworkSize / 2;
        int lateralMarginFireworksBottom = emptySpace - fireworkSize * 3 / 4;

        following.getLayoutParams().width = emptySpace / 3;
        previous.getLayoutParams().width = emptySpace / 3;
        findViewById(R.id.hints).getLayoutParams().width = findViewById(R.id.hints).getLayoutParams().height = hintsSize;
        findViewById(R.id.back).getLayoutParams().width = backWidth;
        findViewById(R.id.back).getLayoutParams().height = backHeight;
        findViewById(R.id.header).getLayoutParams().height = headerHeight;

        hints.getLayoutParams().width = hints.getLayoutParams().height = headerHeight;
        back.getLayoutParams().height = headerHeight;
        back.getLayoutParams().width = backWidth + 2 * hintsMargin;

        findViewById(R.id.fireworkLeftTop).getLayoutParams().width = findViewById(R.id.fireworkLeftTop).getLayoutParams().height = findViewById(R.id.fireworkRightTop).getLayoutParams().width = findViewById(R.id.fireworkRightTop).getLayoutParams().height = fireworkSize;
        findViewById(R.id.fireworkLeftBottom).getLayoutParams().width = findViewById(R.id.fireworkLeftBottom).getLayoutParams().height = findViewById(R.id.fireworkRightBottom).getLayoutParams().width = findViewById(R.id.fireworkRightBottom).getLayoutParams().height = fireworkSize;

        Utility.setMargins(previous, emptySpace / 3, 0, 0, 0);
        Utility.setMargins(following, 0, 0, emptySpace / 3, 0);
        Utility.setMargins(findViewById(R.id.previousLayout), 0, 543 * 65 / 300, 0, 543 * 65 / 300);
        Utility.setMargins(findViewById(R.id.followingLayout), 0, 543 * 65 / 300, 0, 543 * 65 / 300);

        Utility.setMargins(findViewById(R.id.hints), 0, 0, hintsMargin, 0);
        Utility.setMargins(findViewById(R.id.back), hintsMargin, 0, 0, 0);

        Utility.setMargins(findViewById(R.id.fireworkLeftTop), lateralMarginFireworksTop, topMarginFireworksTop, 0, 0);
        Utility.setMargins(findViewById(R.id.fireworkLeftBottom), lateralMarginFireworksBottom, topMarginFireworksBottom, 0, 0);
        Utility.setMargins(findViewById(R.id.fireworkRightTop), 0, topMarginFireworksTop, lateralMarginFireworksTop, 0);
        Utility.setMargins(findViewById(R.id.fireworkRightBottom), 0, topMarginFireworksBottom, lateralMarginFireworksBottom, 0);

        playWon.setVisibility(getQuizData().isSolved() ? View.VISIBLE : View.GONE);

        switch (packageIndex) {
            case Utility.CINEMA:
                initCinemaViews();
                break;
            case Utility.MUSIC:
                initMusicViews();
                break;
            case Utility.VIP:
                initCharacterViews();
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
        playWon.getLayoutParams().width = screenWidth / 5;
        playWon.getLayoutParams().height = screenWidth / 5;

        playWon.setAlpha(0.7f);

        type.getLayoutParams().width = screenWidth;
        type.getLayoutParams().height = screenHeight / 3;

        Utility.setMargins(imageLayout, leftMarginScreen, topMarginScreen, 0, 0);
        Utility.setMargins(visualizerCinema, leftMarginScreen, 0, 0, 0);
        Utility.setMargins(type, leftMarginScreen, topMarginScreen, 0, 0);

        Utility.setMargins(play, leftMarginScreen + screenWidth / 3, topMarginScreen + (screenHeight - (screenWidth / 3)) / 2, 0, 0);
        Utility.setMargins(playWon, leftMarginScreen + screenWidth * 2 / 5, topMarginScreen + (screenHeight - (screenWidth * 2 / 5)) / 2, 0, 0);

        Utility.setMargins(findViewById(R.id.body), 0, Utility.getHeight(this) * 100 / 1280, 0, 0);

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
        int stringTopWidth = Utility.getHeight(this) * 165 / 1356;
        float compressionFactor = 130f / 160f;
        int imageSize = guitarWidth * 310 / 498;


        visualizerMusic.getLayoutParams().width = (int) (stringTopWidth / 0.9f);
        visualizerMusic.getLayoutParams().height = guitarHeight * 605 / 678;
        visualizerMusic.setStroke(Utility.getHeight(this) * 6 / 1356);
        visualizerMusic.setCompressionFactor(compressionFactor);
        visualizerMusic.setColor(Color.rgb(248, 246, 236));

        play.getLayoutParams().width = play.getLayoutParams().height = (int) (stringTopWidth / 0.9f);
        playWon.getLayoutParams().width = playWon.getLayoutParams().height = imageSize / 3;
        playWon.setAlpha(0.7f);
        findViewById(R.id.imageQuiz).getLayoutParams().width = imageSize;
        findViewById(R.id.imageQuiz).getLayoutParams().height = imageSize;

        Utility.setMargins(guitar, center, 0, 0, 0);
        Utility.setMargins(play, guitarHeight * (97 + 10) / 678, guitarHeight * (104 + 10) / 678, 0, 0);

        updateMusicLabels();

    }

    private void initCharacterViews() {
        View character = findViewById(R.id.imageViewCharacter);
        View mouth = findViewById(R.id.imageViewMouth);

        int characterSize = Utility.getWidth(this) * 2 / 3;
        int imageQuizSize = characterSize * 332 / 512;

        int mouthWidth = characterSize * 90 / 512;
        int mouthHeight = mouthWidth * 261 / 611;

        int mouthTopMargin = characterSize * 395 / 512;
        int mouthLeftMargin = characterSize * 212 / 512;
        int imageQuizTopMargin = characterSize * 125 / 512;
        int imageQuizLeftMargin = characterSize * 89 / 512;

        int layoutTopMargin = Utility.getHeight(this) * 110 / 1196;

        play.getLayoutParams().width = play.getLayoutParams().height = (int) (characterSize * 0.18f);
        playWon.getLayoutParams().width = playWon.getLayoutParams().height = (int) (characterSize * 0.18f);
        playWon.setAlpha(0.7f);

        findViewById(R.id.imageQuiz).getLayoutParams().width = findViewById(R.id.imageQuiz).getLayoutParams().height = imageQuizSize;

        character.getLayoutParams().width = characterSize;
        character.getLayoutParams().height = characterSize;
        mouth.getLayoutParams().width = mouthWidth;
        mouth.getLayoutParams().height = mouthHeight;

        Utility.setMargins(play, (int) (characterSize * 0.41f), (int) (characterSize * 0.69f), 0, 0);
        Utility.setMargins(findViewById(R.id.imageQuiz), imageQuizLeftMargin, imageQuizTopMargin, 0, 0);

        Utility.setMargins(mouth, mouthLeftMargin, mouthTopMargin, 0, 0);
        Utility.setMargins(findViewById(R.id.body), 0, layoutTopMargin, 0, 0);
    }

    private void initGameView() {
        gameView.init(getQuizData());
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
        setButtonPlayVisibile(true);

        hints.setVisibility(View.GONE);
        findViewById(R.id.hints).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.textViewWiki)).setText(getQuizData().getWikiDescription());
        ((TextView) findViewById(R.id.textViewWiki)).setTextSize(17);
        ((TextView) findViewById(R.id.textViewWiki)).setMaxLines(4);

        ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(getResources().getIdentifier(getQuizData().getID(), "drawable", getPackageName())));

        findViewById(R.id.buttonWiki).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getQuizData().getWikiURI())));
            }
        });


        switch (packageIndex) {
            case Utility.CINEMA:
                initCinemaWinGraphics();
                break;
            case Utility.MUSIC:
                initMusicWinGraphics();
                findViewById(R.id.imageQuiz).invalidate();
                break;
            case Utility.VIP:
                initCharacterWinGraphics();
                findViewById(R.id.imageQuiz).invalidate();
                break;
        }

        findViewById(R.id.layoutImageQuiz).setVisibility(View.VISIBLE);
        findViewById(R.id.imageQuiz).setVisibility(View.VISIBLE);

    }

    private void initCinemaWinGraphics() {
        setVisualizerVisible(false);
        type.setVisibility(View.GONE);

        ((ImageView) findViewById(R.id.imageViewTV)).setImageDrawable(getResources().getDrawable(R.drawable.tv_simpsons_empty_with_shadow));

        if (getQuizData().getSpotifyURI().length() > 0) {
            findViewById(R.id.buttonSpotify).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonSpotify).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getQuizData().getSpotifyURI())));
                }
            });
        } else {
            findViewById(R.id.buttonSpotify).setVisibility(View.GONE);
        }
    }

    private void initMusicWinGraphics() {
        setVisualizerVisible(true);
        stopVisualizerAnimation();

        if (getQuizData().getSpotifyURI().length() > 0) {
            findViewById(R.id.buttonSpotify).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonSpotify).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getQuizData().getSpotifyURI())));
                }
            });
        } else {
            findViewById(R.id.buttonSpotify).setVisibility(View.GONE);
        }

    }

    private void initCharacterWinGraphics() {
        setVisualizerVisible(true);
        stopVisualizerAnimation();

        ((ImageView) findViewById(R.id.imageViewCharacter)).setImageDrawable(getResources().getDrawable(R.drawable.guess_who_frame_empty));
        findViewById(R.id.imageViewMouth).setVisibility(View.GONE);
        refreshVisualizer = false;
    }

    private void startWinAnimation() {
        Log.d("WIN ANIMATION", " inside");

        TranslateAnimation slideAnimation = new TranslateAnimation(-Utility.getWidth(this) / 2, 0, 0, 0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        AlphaAnimation alphaAnimationButton = new AlphaAnimation(0, 1);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        slideAnimation.setDuration(300);
        alphaAnimation.setDuration(300);
        alphaAnimationButton.setStartOffset(290);
        alphaAnimationButton.setDuration(1);


        switch (packageIndex) {
            case Utility.CINEMA:
                findViewById(R.id.imageQuiz).startAnimation(scaleAnimation);
                findViewById(R.id.win).startAnimation(scaleAnimation);
                playWon.startAnimation(alphaAnimationButton);
                break;
            case Utility.MUSIC:
                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(slideAnimation);
                animationSet.setDuration(300);
                findViewById(R.id.imageQuiz).startAnimation(animationSet);
                findViewById(R.id.win).startAnimation(scaleAnimation);
                playWon.startAnimation(alphaAnimationButton);
                break;
            case Utility.VIP:
                findViewById(R.id.imageQuiz).startAnimation(scaleAnimation);
                findViewById(R.id.win).startAnimation(scaleAnimation);
                playWon.startAnimation(alphaAnimationButton);
                break;
        }
        startFireworkAnimation();
    }

    private void startFireworkAnimation() {
        fireworkCounter = 0;
        fireworkAnimation = true;
        audioPlayer.playFireworks();
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
            case Utility.CINEMA:
                visualizerCinema.startAnimation();
                break;
            case Utility.MUSIC:
                visualizerMusic.startAnimation();
                break;
            case Utility.VIP:
                visualizerCharacter.startAnimation();
                break;
        }
        setButtonPlayVisibile(false);
    }

    private void stopVisualizerAnimation() {
        switch (packageIndex) {
            case Utility.CINEMA:
                visualizerCinema.stopAnimation();
                break;
            case Utility.MUSIC:
                visualizerMusic.stopAnimation();
                break;
            case Utility.VIP:
                visualizerCharacter.stopAnimation();
                break;
        }
//        play.setVisibility(quizData.isSolved() ? View.GONE : View.VISIBLE);
        setButtonPlayVisibile(true);
    }

    private void setButtonPlayVisibile(boolean visible) {
        if (getQuizData().isSolved()) {
//            playWon.setVisibility(visible ? View.VISIBLE : View.GONE);
            playWon.setVisibility(View.VISIBLE);
            playWon.setImageDrawable(getResources().getDrawable(visible ? R.drawable.button_play_won : R.drawable.button_pause));
            Log.d("BUTTON_PLAY_WON", " " + visible);
        } else {
            play.setVisibility(visible ? View.VISIBLE : View.GONE);
            Log.d("BUTTON_PLAY", " " + visible);
        }
    }


    public void togglePlay() {
        if (!audioPlayer.isPlayerPlaying()) {
            audioPlayer.startPlayer();
            if (getQuizData().isSolved()) {
                setButtonPlayVisibile(false);
            } else {
                startVisualizerAnimation();
            }

        } else {
            audioPlayer.pausePlayer();
            if (getQuizData().isSolved()) {
                setButtonPlayVisibile(true);
            } else {
                stopVisualizerAnimation();
            }
        }
    }

    private void cleanUp() {
        stopVisualizerAnimation();
        refreshVisualizer = false;
        refreshGameView = false;
        shakeHints = false;

        audioPlayer.invalidatePlayer();

    }


    private void changeToRevealOneLetterMode() {
        gameView.changeToRevealOneLetterMode();
        Utility.setMargins(revealOneLetterLayout, Utility.convertDpToPixels(this, 16), gameView.getMaxY() + Utility.convertDpToPixels(this, 16), Utility.convertDpToPixels(this, 16), 0);
        revealOneLetterLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.followingLayout).setVisibility(View.GONE);
        findViewById(R.id.previousLayout).setVisibility(View.GONE);
    }

    public void changeToGameMode() {
        revealOneLetterLayout.setVisibility(View.GONE);
        findViewById(R.id.followingLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.previousLayout).setVisibility(View.VISIBLE);
        gameView.changeToGameMode();
    }


    private void startShakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        hints.startAnimation(shake);
    }


    public void setVisualizerVisible(boolean isVisible) {
        refreshVisualizer = isVisible;
        switch (packageIndex) {
            case Utility.CINEMA:
                visualizerCinema.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                break;
            case Utility.MUSIC:
                visualizerMusic.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                break;
            case Utility.VIP:
                visualizerCharacter.setVisible(isVisible);
                break;
        }
    }

    private void goToFollowingQuiz() {
        if (quizIndex < 14 && !gameView.isInRevalOneLetterMode()) {
            quizIndex++;
            reset();
        }
    }

    private void goToPreviousQuiz() {
        if (quizIndex > 0 && !gameView.isInRevalOneLetterMode()) {
            quizIndex--;
            reset();
        }
    }

    private void reset() {
        audioPlayer.releasePlayer();
        stopVisualizerAnimation();
        refreshGameView = false;
        gameView.resetSprites();

        resetImages();
        initGameView();
        initButtons();

        initSound(!getQuizData().isSolved());

        play.setVisibility(View.GONE);
        playWon.setVisibility(getQuizData().isSolved() ? View.VISIBLE : View.GONE);
        playWon.setImageDrawable(getResources().getDrawable(R.drawable.button_play_won));
    }


    private void resetImages() {
        Log.d("RESET IMAGES", " inside");
        findViewById(R.id.win).setVisibility(View.GONE);
        hints.setVisibility(View.VISIBLE);
        findViewById(R.id.hints).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutImageQuiz).setVisibility(View.GONE);


//        int res = getResources().getIdentifier(getQuizData().getID(), "drawable", getPackageName());
//        try {
//            ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(res));
//        } catch (Exception e) {
//            ((ImageView) findViewById(R.id.imageQuiz)).setImageDrawable(getResources().getDrawable(R.drawable.twenty_century_fox));
//        }

        refreshVisualizer = true;

        switch (packageIndex) {
            case Utility.CINEMA:
                ((ImageView) findViewById(R.id.imageViewTV)).setImageDrawable(getResources().getDrawable(R.drawable.tv_simpsons_with_shadow));
                type.setVisibility(getQuizData().isSolved() ? View.GONE : View.VISIBLE);
                type.setText(getQuizData().getType());
                setVisualizerVisible(!getQuizData().isSolved());
                break;
            case Utility.MUSIC:
                setVisualizerVisible(true);
                updateMusicLabels();
                break;
            case Utility.VIP:
                setVisualizerVisible(true);
                ((ImageView) findViewById(R.id.imageViewCharacter)).setImageDrawable(getResources().getDrawable(R.drawable.guess_who_frame));
                findViewById(R.id.imageViewMouth).setVisibility(View.VISIBLE);
                break;
        }
    }

    private void openHintDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        switch (getPackageIndex()) {
            case Utility.CINEMA:
                dialog.setContentView(R.layout.dialog_hints_cinema);
                break;
            case Utility.MUSIC:
                dialog.setContentView(R.layout.dialog_hints_music);
                break;
            case Utility.VIP:
                dialog.setContentView(R.layout.dialog_hints_character);
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

    private void updateMusicLabels() {
        int guitarHeight = Utility.getHeight(this) / 2;
        int labelLayoutHeight = guitarHeight * 70 / 678;

        TextView song = (TextView) findViewById(R.id.textViewSong);
        TextView artist = (TextView) findViewById(R.id.textViewArtist);

//        song.setTextSize(getQuizData().getType().toLowerCase().equals("song") ? Utility.pixelsToSp(this, labelLayoutHeight * 7 / 10) : Utility.pixelsToSp(this, labelLayoutHeight / 2));
//        song.setTextColor(getQuizData().getType().toLowerCase().equals("song") ? Color.parseColor("#ffffff") : Color.parseColor("#80000000"));
//
//        artist.setTextSize(getQuizData().getType().toLowerCase().equals("artist") ? Utility.pixelsToSp(this, labelLayoutHeight * 7 / 10) : Utility.pixelsToSp(this, labelLayoutHeight / 2));
//        artist.setTextColor(getQuizData().getType().toLowerCase().equals("artist") ? Color.parseColor("#ffffff") : Color.parseColor("#80000000"));

        setAlpha(song,getQuizData().getType().toLowerCase().equals("song") ? 1 : 0.4f);
        setAlpha(artist,getQuizData().getType().toLowerCase().equals("song") ? 1 : 0.4f);

        Utility.setMargins(findViewById(R.id.labelsLayout), 0, 0, 0, guitarHeight * 28 / 678);
        findViewById(R.id.labelsLayout).getLayoutParams().height = labelLayoutHeight;
    }

    private void setAlpha(View view, float alpha) {
        AlphaAnimation alphaAnim = new AlphaAnimation(alpha, alpha);
        alphaAnim.setDuration(0); // Make animation instant
        alphaAnim.setFillAfter(true); // Tell it to persist after the animation ends
        // And then on your layout
        view.startAnimation(alphaAnim);
    }


    public QuizData getQuizData() {
        return PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(quizIndex);
    }

    public int getPackageIndex() {
        return packageIndex;
    }


}
