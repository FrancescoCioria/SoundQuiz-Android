package com.mosquitolabs.soundquiz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class LetterSprite {

    private final static float ANIMATION_TIME = 300f; // (millis)
    private int INDEX;
    private String letter;

    private GameView gameView;

    private boolean isHome = true;
    private boolean isAnimating = false;
    private Point home = new Point();
    private Point position = new Point();
    private Point endPosition = new Point();
    private Paint paint = new Paint(Color.WHITE);
    private int size;


    private Bitmap bmp;

    public LetterSprite(GameView gameView, int x, int y, int size, int index, String letter) {
        home.x = position.x = x;
        home.y = position.y = y;
        this.size = size;
        this.gameView = gameView;
        INDEX = index;
        this.letter = letter;

        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(gameView.getActivityContext().getResources(), R.drawable.frozen_blur), size, size, false);
    }

    private void update() {
        if (isAnimating) {
            boolean isToTheLeft = endPosition.x > home.x;
            float animationCycles = (ANIMATION_TIME / (1000 / Utility.getFPS()));
            float deltaX = (endPosition.x - home.x) / animationCycles;
            float deltaY = (endPosition.y - home.y) / animationCycles;
            Log.d("DELTA", " x: " + deltaX + "   y: " + deltaY);

            if ((isToTheLeft && (position.x + deltaX < endPosition.x && position.y + deltaY < endPosition.y)) || (!isToTheLeft && (position.x + deltaX > endPosition.x && position.y + deltaY > endPosition.y))) {
                position.x += deltaX;
                position.y += deltaY;
            } else {
                isAnimating = false;
                position.x = endPosition.x;
                position.y = endPosition.y;
            }

        }
    }

    public void onDraw(Canvas canvas) {
        update();

        canvas.drawRect(position.x, position.y, position.x + size, position.y + size, paint);
    }

    private boolean isCollision(float x2, float y2) {
        return x2 > position.x && x2 < position.x + size && y2 > position.y && y2 < position.y + size;
    }

    public void checkCollision(float x2, float y2) {
        if (isCollision(x2, y2) && !isAnimating) {
            if (isHome) {
                moveTo(getFirstEmptySpace());
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
        isAnimating = true;
        isHome = false;
        this.endPosition.x = position.x;
        this.endPosition.y = position.y;

    }

    private void goBackHome() {
        isAnimating = true;
        isHome = true;
        emptyCurrentSpace();
        this.endPosition.x = home.x;
        this.endPosition.y = home.y;
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
                return gameView.getLetterSpaces().get(i).position;
            }
        }
        return null;
    }
}