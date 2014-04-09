package com.mosquitolabs.soundquiz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Vibrator;
import android.util.Log;

public class LetterSprite {

    private final static float ANIMATION_TIME = 230f; // (millis)
    private int INDEX;
    private int SIZE;
    private int SPACE_SIZE;
    private String letter;

    private GameView gameView;

    private boolean isHome = true;
    private boolean isAnimating = false;
    private Point home = new Point();
    private Point position = new Point();
    private Point finalPosition = new Point();
    private Point initialPosition = new Point();
    private Paint rectPaint = new Paint();
    private Paint textPaint = new Paint();
    private int currentSize;
    private int finalSize;
    private int initialSize;
    private  Vibrator vibrator;


    private Bitmap bmp;

    public LetterSprite(GameView gameView, int x, int y, int size, int spaceSize, int index, String letter) {
        home.x = position.x = x;
        home.y = position.y = y;
        this.SIZE = size;
        this.SPACE_SIZE = spaceSize;
        this.gameView = gameView;
        INDEX = index;
        this.letter = letter;

        rectPaint.setColor(Color.rgb(217, 152, 110));
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(3);
        textPaint.setAntiAlias(true);

        currentSize = SIZE;
        vibrator = (Vibrator) gameView.getActivityContext().getSystemService(Context.VIBRATOR_SERVICE);

        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(gameView.getActivityContext().getResources(), R.drawable.frozen_blur), size, size, false);
    }

    private void update() {
        if (isAnimating) {
            boolean isGoingHome = finalPosition.x == home.x && finalPosition.y == home.y;
            boolean isGoingLeft = initialPosition.x > finalPosition.x;
            boolean sameX = initialPosition.x == finalPosition.x;
            float animationCycles = (ANIMATION_TIME / (1000 / Utility.getFPS()));
            int deltaX = (int) (Math.abs(finalPosition.x - initialPosition.x) / animationCycles);
            int deltaY = (int) (Math.abs(finalPosition.y - initialPosition.y) / animationCycles);
            float deltaScale = (initialSize - finalSize) / animationCycles;
//            Log.d("DELTA", " x: " + deltaX + "   y: " + deltaY);
//            Log.d("DELTAX", "" + Math.abs(finalPosition.x - initialPosition.x));

            boolean animate = true;
            if (isGoingHome && isGoingLeft) {
                position.x -= deltaX;
                position.y += deltaY;

                if (position.x < finalPosition.x || position.y > finalPosition.y) {
                    animate = false;
                } else if (currentSize < finalSize) {
                    currentSize -= deltaScale;
                }

            } else if (isGoingHome && (!isGoingLeft || sameX)) {
                position.x += deltaX;
                position.y += deltaY;


                if (position.x > finalPosition.x || position.y > finalPosition.y) {
                    animate = false;
                } else if (currentSize < finalSize) {
                    currentSize -= deltaScale;
                }


            } else if (!isGoingHome && (isGoingLeft || sameX)) {
                position.x -= deltaX;
                position.y -= deltaY;

                if (position.x < finalPosition.x || position.y < finalPosition.y) {
                    animate = false;
                } else if (currentSize > finalSize) {
                    currentSize -= deltaScale;
                }

            } else if (!isGoingHome && !isGoingLeft) {
                position.x += deltaX;
                position.y -= deltaY;

                if (position.x > finalPosition.x || position.y < finalPosition.y) {
                    animate = false;
                } else if (currentSize > finalSize) {
                    currentSize -= deltaScale;
                }
            }

            if (!animate) {
                isAnimating = false;
                position.x = finalPosition.x;
                position.y = finalPosition.y;
                currentSize = finalSize;
            }

        }
    }

    public void onDraw(Canvas canvas) {
        update();

        canvas.drawRect(position.x, position.y, position.x + currentSize, position.y + currentSize, rectPaint);

        textPaint.setTextSize(currentSize / 2);
        float correctionX = currentSize / 10;
        canvas.drawText(letter.toUpperCase(), position.x + currentSize / 4 + correctionX, position.y + currentSize * 3 / 4, textPaint);

    }

    private boolean isCollision(float x2, float y2) {
        return x2 > position.x && x2 < position.x + currentSize && y2 > position.y && y2 < position.y + currentSize;
    }

    public void checkCollision(float x2, float y2) {
        if (isCollision(x2, y2) && !isAnimating) {
            if (isHome) {
                Point destination = getFirstEmptySpace();
                if (destination != null) {
                    moveTo(destination);
                    checkAnswer();
                } else {
                    Utility.shortToast("Remove some letters first", gameView.getActivityContext());
                }
            } else {
                goBackHome();
            }
        }

    }

    public Point getHome() {
        return home;
    }

    public boolean isHome() {
        return isHome;
    }

    private void moveTo(Point position) {
        this.finalPosition.x = position.x;
        this.finalPosition.y = position.y;
        initialPosition.x = this.position.x;
        initialPosition.y = this.position.y;
        finalSize = SPACE_SIZE;
        initialSize = currentSize;
        isAnimating = true;
        isHome = false;

        vibrator.vibrate(100);
    }

    private void goBackHome() {
        emptyCurrentSpace();
        this.finalPosition.x = home.x;
        this.finalPosition.y = home.y;
        initialPosition.x = position.x;
        initialPosition.y = position.y;
        finalSize = SIZE;
        initialSize = currentSize;
        isAnimating = true;
        isHome = true;
        vibrator.vibrate(100);
    }


    private void emptyCurrentSpace() {
        for (int i = 0; i < gameView.getLetterSpaces().size(); i++) {
            if (gameView.getLetterSpaces().get(i).letterSpriteContained == INDEX) {
                gameView.getLetterSpaces().get(i).letterSpriteContained = -1;
            }
        }
    }

    private Point getFirstEmptySpace() {
        for (int i = 0; i < gameView.getLetterSpaces().size(); i++) {
            if (gameView.getLetterSpaces().get(i).letterSpriteContained < 0) {
                gameView.getLetterSpaces().get(i).letterSpriteContained = INDEX;
                Log.d("FIRSTEM", "  " + INDEX + "   letter " + gameView.getLetterSprites().get(gameView.getLetterSpaces().get(i).letterSpriteContained).letter);
                return gameView.getLetterSpaces().get(i).position;
            }
        }
        return null;
    }

    private void checkAnswer() {
        String userAnswer = "";
        for (int i = 0; i < gameView.getLetterSpaces().size(); i++) {
            if (gameView.getLetterSpaces().get(i).letterSpriteContained < 0) {
                return;
            } else {
                userAnswer += gameView.getLetterSprites().get(gameView.getLetterSpaces().get(i).letterSpriteContained).letter;
            }
        }
        QuizActivity quizActivity = ((QuizActivity) gameView.getActivityContext());

        if (gameView.getAnswer().equals(userAnswer)) {
            quizActivity.startWinActivity();
        } else {
            Utility.shortToast("Wrong answer!", gameView.getActivityContext());
        }
    }


}