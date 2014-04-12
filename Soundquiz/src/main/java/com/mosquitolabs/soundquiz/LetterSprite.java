package com.mosquitolabs.soundquiz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Vibrator;

public class LetterSprite {

    private final static float ANIMATION_TIME = 230f; // (millis)
    private int INDEX;
    private int SIZE;
    private int SPACE_SIZE;
    private String letter;

    private GameView gameView;

    private boolean isHome = true;
    private boolean isAnimating = false;
    private MyPoint home = new MyPoint();
    private MyPoint position = new MyPoint();
    private MyPoint finalPosition = new MyPoint();
    private MyPoint initialPosition = new MyPoint();
    private Paint rectPaint = new Paint();
    private Paint textPaint = new Paint();
    private RectF roundedRect = new RectF();
    private int currentSize;
    private int finalSize;
    private int initialSize;
    private int textHeight;
    private Vibrator vibrator;
    private Rect bounds = new Rect();


    private Bitmap bmp;

    public LetterSprite(GameView gameView, int x, int y, int size, int spaceSize, int index, String letter) {
        home.x = position.x = x;
        home.y = position.y = y;
        this.SIZE = size;
        this.SPACE_SIZE = spaceSize;
        this.gameView = gameView;
        this.INDEX = index;
        this.letter = letter;
        this.currentSize = SIZE;

//        textPaint.setColor(Color.rgb(74, 32, 0));
        textPaint.setColor(Color.rgb(0, 33, 23));
        textPaint.setStrokeWidth(3);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(currentSize / 2);
        textPaint.getTextBounds("T", 0, 1, bounds);
        textHeight = bounds.height();
        textPaint.getTextBounds(letter.toUpperCase(), 0, 1, bounds);

//        rectPaint.setColor(Color.rgb(217, 152, 110));
        rectPaint.setColor(Color.rgb(96, 160, 142));
        rectPaint.setAntiAlias(true);

        roundedRect = new RectF(new Rect(position.getX(), position.getY(), position.getX() + currentSize, position.getY() + currentSize));

        vibrator = (Vibrator) gameView.getActivityContext().getSystemService(Context.VIBRATOR_SERVICE);

//        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(gameView.getActivityContext().getResources(), R.drawable.frozen_blur), size, size, false);
    }

    private void update() {
        if (isAnimating) {
            boolean isGoingHome = finalPosition.x == home.x && finalPosition.y == home.y;
            boolean isGoingLeft = initialPosition.x > finalPosition.x;
            boolean sameX = initialPosition.x == finalPosition.x;
            float animationCycles = (ANIMATION_TIME / (1000 / Utility.getFPS()));
            float deltaX = Math.abs(finalPosition.x - initialPosition.x) / animationCycles;
            float deltaY = Math.abs(finalPosition.y - initialPosition.y) / animationCycles;
            float deltaScale = (initialSize - finalSize) / animationCycles;

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

            textPaint.setTextSize(currentSize / 2);
            textPaint.getTextBounds("T", 0, 1, bounds);
            textHeight = bounds.height();
            textPaint.getTextBounds(letter.toUpperCase(), 0, 1, bounds);

            roundedRect = new RectF(new Rect(position.getX(), position.getY(), position.getX() + currentSize, position.getY() + currentSize));
        }
    }

    public void onDraw(Canvas canvas) {
        update();

        canvas.drawRoundRect(roundedRect, currentSize / 15, currentSize / 15, rectPaint);

        canvas.drawText(letter.toUpperCase(), position.x + currentSize / 2f - textPaint.measureText(letter.toUpperCase()) / 2f, position.y + currentSize / 2f + textHeight / 2f, textPaint);
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
        return new Point(home.getX(), home.getY());
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

        vibrator.vibrate(30);
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
        vibrator.vibrate(30);
    }


    private void emptyCurrentSpace() {
        for (int i = 0; i < gameView.getLetterSpaces().size(); i++) {
            if (gameView.getLetterSpaces().get(i).letterSpriteContained == INDEX) {
                gameView.getLetterSpaces().get(i).letterSpriteContained = -1;
                return;
            }
        }
    }

    private Point getFirstEmptySpace() {
        for (int i = 0; i < gameView.getLetterSpaces().size(); i++) {
            if (gameView.getLetterSpaces().get(i).letterSpriteContained < 0) {
                gameView.getLetterSpaces().get(i).letterSpriteContained = INDEX;
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

        if (gameView.getAnswer().equals(userAnswer)) {
            gameView.getActivityContext().initWinPage();
        } else {
            Utility.shortToast("Wrong answer!", gameView.getActivityContext());
            vibrator.vibrate(150);
        }
    }

    public void reset() {
        emptyCurrentSpace();
        this.position.x = home.x;
        this.position.y = home.y;
        currentSize = SIZE;

        roundedRect = new RectF(new Rect(position.getX(), position.getY(), position.getX() + currentSize, position.getY() + currentSize));
        textPaint.setTextSize(currentSize / 2);
        textPaint.getTextBounds("T", 0, 1, bounds);
        textHeight = bounds.height();
        textPaint.getTextBounds(letter.toUpperCase(), 0, 1, bounds);

        isAnimating = true;
        isHome = true;
    }

    public void setPosition() {
        Point newPosition = getFirstEmptySpace();
        position.x = newPosition.x;
        position.y = newPosition.y;
        currentSize = SPACE_SIZE;

        roundedRect = new RectF(new Rect(position.getX(), position.getY(), position.getX() + currentSize, position.getY() + currentSize));
        textPaint.setTextSize(currentSize / 2);
        textPaint.getTextBounds("T", 0, 1, bounds);
        textHeight = bounds.height();
        textPaint.getTextBounds(letter.toUpperCase(), 0, 1, bounds);

        isAnimating = false;
        isHome = false;
    }

    public String getLetter() {
        return letter;
    }

    static class MyPoint {
        float x;
        float y;

        private int getX() {
            return (int) x;
        }

        private int getY() {
            return (int) y;
        }
    }

}