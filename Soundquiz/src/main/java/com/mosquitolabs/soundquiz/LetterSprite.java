package com.mosquitolabs.soundquiz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Vibrator;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;

public class LetterSprite {

    private final static float ANIMATION_TIME = 230f; // (millis)
    private int INDEX;
    private int SIZE;
    private int SPACE_SIZE;
    private String letter;

    private GameView gameView;

    private boolean isHome = true;
    private boolean isAnimating = false;
    private boolean isVisible = true;
    private boolean isClickable = true;
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

    public LetterSprite(GameView gameView, int x, int y, int size, int spaceSize, int index, String letter) {
        home.x = position.x = x;
        home.y = position.y = y;
        this.SIZE = size;
        this.SPACE_SIZE = spaceSize;
        this.gameView = gameView;
        this.INDEX = index;
        this.letter = letter;
        this.currentSize = SIZE;

        textPaint.setStrokeWidth(3);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(currentSize / 2);
        textPaint.getTextBounds("T", 0, 1, bounds);
        textHeight = bounds.height();
        textPaint.getTextBounds(letter.toUpperCase(), 0, 1, bounds);

        rectPaint.setAntiAlias(true);

        resetColors();

        roundedRect = new RectF(new Rect(position.getX(), position.getY(), position.getX() + currentSize, position.getY() + currentSize));

        vibrator = (Vibrator) gameView.getActivityContext().getSystemService(Context.VIBRATOR_SERVICE);
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
        if (!isVisible) {
            return;
        }

        update();

        canvas.drawRoundRect(roundedRect, currentSize / 15, currentSize / 15, rectPaint);

        canvas.drawText(letter.toUpperCase(), position.x + currentSize / 2f - textPaint.measureText(letter.toUpperCase()) / 2f, position.y + currentSize / 2f + textHeight / 2f, textPaint);
    }

    private boolean isCollision(float x2, float y2) {
        return x2 > position.x && x2 < position.x + currentSize && y2 > position.y && y2 < position.y + currentSize;
    }

    public void checkCollision(float x2, float y2) {
        if (!isClickable || isAnimating) {
            return;
        }

        if (isCollision(x2, y2)) {
            if (isHome) {
                Point destination = getFirstEmptySpace();
                if (destination != null) {
                    moveTo(destination);
                    gameView.checkAnswer();
                } else {
                    AudioPlayer.getIstance().playWrong();
                }
            } else {
                goBackHome();
            }
        }
    }

    public void resetColors(){
        switch (gameView.getActivityContext().getPackageIndex()) {
            case Utility.CINEMA:
                rectPaint.setColor(gameView.getResources().getColor(R.color.tile_cinema));
                textPaint.setColor(gameView.getResources().getColor(R.color.tile_text_cinema));
                break;
            case Utility.MUSIC:
                rectPaint.setColor(gameView.getResources().getColor(R.color.tile_music));
                textPaint.setColor(gameView.getResources().getColor(R.color.tile_text_music));
                break;
            case Utility.VIP:

                break;
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

        AudioPlayer.getIstance().playSelect();
//        vibrator.vibrate(30);
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

        AudioPlayer.getIstance().playRemove();
//        vibrator.vibrate(30);
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



    public void reset() {
        if (isClickable && isVisible) {
            emptyCurrentSpace();
            this.position.x = home.x;
            this.position.y = home.y;
            currentSize = SIZE;

            roundedRect = new RectF(new Rect(position.getX(), position.getY(), position.getX() + currentSize, position.getY() + currentSize));
            textPaint.setTextSize(currentSize / 2);
            textPaint.getTextBounds("T", 0, 1, bounds);
            textHeight = bounds.height();
            textPaint.getTextBounds(letter.toUpperCase(), 0, 1, bounds);

            isAnimating = false;
            isHome = true;
        }
    }

    public void setPosition(int x, int y) {
        position.x = x;
        position.y = y;
        currentSize = SPACE_SIZE;

        roundedRect = new RectF(new Rect(position.getX(), position.getY(), position.getX() + currentSize, position.getY() + currentSize));
        textPaint.setTextSize(currentSize / 2);
        textPaint.getTextBounds("T", 0, 1, bounds);
        textHeight = bounds.height();
        textPaint.getTextBounds(letter.toUpperCase(), 0, 1, bounds);

        isAnimating = false;
        isHome = false;
    }

    public void goToFirstEmpty() {
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

    public void setVisible(boolean visible) {
        isVisible = visible;
        if (!isVisible) {
            isClickable = false;
        }
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public String getLetter() {
        return letter;
    }

    public int getIndex() {
        return INDEX;
    }

    public boolean isFix() {
        return !isClickable && isVisible;
    }

    public void setFix() {
        isVisible = true;
        isClickable = false;
        textPaint.setColor(Color.rgb(34, 139, 34));
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