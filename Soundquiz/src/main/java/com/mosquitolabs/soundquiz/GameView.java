package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by francesco on 4/6/14.
 */
public class GameView extends View {
    private final static int GAP = 5;
    private final static int ROW_GAP = 10;
    private int MARGIN_Y = 10;
    //    private int SIZE;
    private int MAX_ITEMS = 10;
    private final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "y", "x", "z"};

    private Paint paint = new Paint();
    private Activity context;


    private ArrayList<LetterSpace> letterSpaces = new ArrayList<LetterSpace>();
    private ArrayList<LetterSprite> letterSprites = new ArrayList<LetterSprite>();

    private boolean stopGameView = false;
    private boolean firstDraw = true;

    private Runnable handlerTask;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        paint.setColor(Color.WHITE);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (firstDraw) {
            firstDraw = false;
            init(((QuizActivity) context).getQuizData());
        }

        for (LetterSpace letterSpace : letterSpaces) {
            canvas.drawRect(letterSpace.position.x, letterSpace.position.y, letterSpace.position.x + letterSpace.size, letterSpace.position.y + letterSpace.size, paint);
        }

        for (LetterSprite letterSprite : letterSprites) {
            letterSprite.onDraw(canvas);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (LetterSprite letterSprite : letterSprites) {
            letterSprite.checkCollision(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void refresh() {
        invalidate();
    }


    public void init(final QuizData quizData) {
        initLetterSpaces(quizData);
        initLetterSprites(quizData);

    }

    private void initLetterSpaces(QuizData quizData) {
        for (String row : quizData.getRows()) {
            String[] words = row.split(" ");
            int maxItems = 0;

            for (String word : words) {
                maxItems += word.length();
            }

            if (maxItems > MAX_ITEMS) {
                MAX_ITEMS = maxItems;
            }
        }

        int size = (int) ((0.8f * Utility.getWidth(this.context)) / (MAX_ITEMS + 2));
        int currentRow = 0;
        for (String row : quizData.getRows()) {
            String[] words = row.split(" ");
            boolean firstWord = true;
            int marginX = (Utility.getWidth(this.context) - (row.length() * size + (row.length() - 1) * GAP)) / 2;
            for (String word : words) {
                for (int z = 0; z < word.length(); z++) {
                    LetterSpace letterSpace = new LetterSpace();
                    if (z == 0) {
                        if (firstWord) {
                            firstWord = false;
                            letterSpace.position.x = marginX;
                        } else {
                            letterSpace.position.x = letterSpaces.get(letterSpaces.size() - 1).position.x + (size + GAP) * 2;
                        }
                    } else {
                        letterSpace.position.x = letterSpaces.get(letterSpaces.size() - 1).position.x + (size + GAP);
                    }

                    letterSpace.position.y = currentRow * (size + ROW_GAP);
                    letterSpace.size = size;
                    letterSpaces.add(letterSpace);

                }
            }
            currentRow++;
        }
    }


    private void initLetterSprites(QuizData quizData) {
        String answer = quizData.getAnswers().get(0);
        answer = answer.toLowerCase().replace(" ", "");
        ArrayList<String> letters = new ArrayList<String>();
        for (int i = 0; i < answer.length(); i++) {
            String letter = String.valueOf(answer.charAt(i));
            letters.add(letter);
        }

        for (int z = answer.length(); z <= 20; z++) {
            Random rnd = new Random();
            letters.add(alphabet[rnd.nextInt(26)]);
        }

        Collections.shuffle(letters);
        Log.d("SPRITES", "letters: " + letters.size());

        int counter = 0;
        int currentRow = 0;
        int size = (int) (0.9f * Utility.getWidth(context) / (letters.size() / 2));
        int marginX = (Utility.getWidth(context) - (10 * size + 9 * GAP)) / 2;

        for (String letter : letters) {
            int positionX = 0;
            int positionY = getHeight() - (MARGIN_Y + size * (currentRow + 1) + ROW_GAP * currentRow);

            if (counter == 0) {
                positionX += marginX;
                Log.d("SPRITE", "1    " + marginX);
            } else if (counter == letters.size() / 2) {
                currentRow++;
                positionX += marginX;
                Log.d("SPRITE", "11   " + marginX);
            } else {
                positionX += letterSprites.get(letterSprites.size() - 1).getHome().x + (size + GAP);
            }

            LetterSprite letterSprite = new LetterSprite(this, positionX, positionY, size, counter, letter);
            letterSprites.add(letterSprite);
            counter++;
        }

    }


    public void startLoop() {
        stopGameView = false;
        final Handler handler = new Handler();
        handlerTask = new Runnable() {
            @Override
            public void run() {
                refresh();
                if (!stopGameView) {
                    handler.postDelayed(handlerTask, 1000 / Utility.getFPS());
                }
            }
        };
        handlerTask.run();
    }

    public void stopLoop() {
        stopGameView = true;
    }

    public Context getActivityContext() {
        return context;
    }

    public ArrayList<LetterSpace> getLetterSpaces() {
        return letterSpaces;
    }


    static class LetterSpace {
        int size;
        int letterSpriteContained = -1;
        Point position = new Point();

    }




}
