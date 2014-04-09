package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameView extends View {
    private int MARGIN_Y = 30;
    private int SPACE_SIZE;
    private final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "y", "x", "z"};

    private Paint paint = new Paint();
    private Activity context;

    private ArrayList<LetterSpace> letterSpaces = new ArrayList<LetterSpace>();
    private ArrayList<LetterSprite> letterSprites = new ArrayList<LetterSprite>();

    private boolean stopGameView = false;
    private boolean firstDraw = true;

    private Runnable handlerTask;

    private String answer;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        paint.setColor(Color.rgb(117, 50, 0));

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
        String answer = ((QuizActivity) context).getQuizData().getAnswers().get(0);
        this.answer = answer.toLowerCase().replace(" ", "").replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");

        initLetterSpaces(quizData);
        initLetterSprites();
    }

    private void initLetterSpaces(QuizData quizData) {
        int maxItems = 0;
        for (String row : quizData.getRows()) {
            row = row.replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");
            String[] words = row.split(" ");
            int counter = 0;
            for (String word : words) {
                counter += word.length();
            }

            if (counter > maxItems) {
                maxItems = counter;
            }
        }
        float percentage = 0.85f;

        if (maxItems > 14) {
            percentage = 0.92f;
        } else if (maxItems > 10) {
            percentage = 0.88f;
        }

        int fullSize = Math.min((int) (percentage * getWidth() / (maxItems + 2)), (getWidth() / 13));
        SPACE_SIZE = fullSize * 90 / 100;
        int GAP = fullSize - SPACE_SIZE;
        int ROW_GAP = SPACE_SIZE / 5;

        int currentRow = 0;
        for (String row : quizData.getRows()) {
            row = row.replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");
            String[] words = row.split(" ");
            boolean firstWord = true;
            int marginX = (getWidth() - (row.replace(" ", "").length() * SPACE_SIZE + (row.length() - row.replace(" ", "").length()) * SPACE_SIZE / 2 + (row.length() - (row.length() - row.replace(" ", "").length()) * 2 - 1) * GAP)) / 2;
            for (String word : words) {
                for (int z = 0; z < word.length(); z++) {
                    LetterSpace letterSpace = new LetterSpace();
                    if (z == 0) {
                        if (firstWord) {
                            firstWord = false;
                            letterSpace.position.x = marginX;
                        } else {
                            letterSpace.position.x = letterSpaces.get(letterSpaces.size() - 1).position.x + (SPACE_SIZE * 3 / 2);
                        }
                    } else {
                        letterSpace.position.x = letterSpaces.get(letterSpaces.size() - 1).position.x + (SPACE_SIZE + GAP);
                    }
                    int marginY = (int) ((getHeight() / 15) / (1 + quizData.getRows().size() * 0.5f));
                    letterSpace.position.y = currentRow * (SPACE_SIZE + ROW_GAP) + marginY;
                    letterSpace.size = SPACE_SIZE;
                    letterSpaces.add(letterSpace);

                }
            }
            currentRow++;
        }
    }


    private void initLetterSprites() {
        ArrayList<String> letters = new ArrayList<String>();
        int numberOfRows = 2;
        for (int i = 0; i < answer.length(); i++) {
            String letter = String.valueOf(answer.charAt(i));
            letters.add(letter);
        }

        int numberOfSprites = 12;

        if (answer.length() > 18) {
            numberOfSprites = (answer.length() - answer.length() % 2) + 2;
        } else if (answer.length() > 14) {
            numberOfSprites = 20;
        } else if (answer.length() > 10) {
            numberOfSprites = 16;
        }

        int fullSize = (int) (0.95f * getWidth() / (numberOfSprites / 2));
        if (fullSize < getWidth() / 17 || fullSize < Utility.convertDpToPixels(getActivityContext(), 40)) {
            while (numberOfSprites % 3 != 0) {
                numberOfSprites++;
            }
            fullSize = (int) (0.95f * getWidth() / (numberOfSprites / 3));
            numberOfRows = 3;
        }

        int SPRITE_SIZE = fullSize * 90 / 100;
        int GAP = fullSize - SPRITE_SIZE;
        int ROW_GAP = SPRITE_SIZE / 5;

        for (int z = answer.length(); z < numberOfSprites; z++) {
            Random rnd = new Random();
            letters.add(alphabet[rnd.nextInt(alphabet.length)]);
        }
        Collections.shuffle(letters);

        int counter = 0;
        int currentRow = 0;
        int marginX = (getWidth() - (numberOfSprites * SPRITE_SIZE / numberOfRows + ((numberOfSprites / numberOfRows) - 1) * GAP)) / 2;

        for (String letter : letters) {
            int positionX = 0;
            if (counter != 0 && counter == numberOfSprites / numberOfRows) {
                counter = 0;
                currentRow++;
            }

            if (counter == 0) {
                positionX += marginX;
            } else {
                positionX += letterSprites.get(letterSprites.size() - 1).getHome().x + (SPRITE_SIZE + GAP);
            }
            int positionY = getHeight() - (marginX + SPRITE_SIZE * (currentRow + 1) + ROW_GAP * currentRow);

            int index = counter + currentRow * (numberOfSprites / numberOfRows);

            LetterSprite letterSprite = new LetterSprite(this, positionX, positionY, SPRITE_SIZE, SPACE_SIZE, index, letter);
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

    public ArrayList<LetterSprite> getLetterSprites() {
        return letterSprites;
    }

    public String getAnswer() {
        return answer;
    }

    static class LetterSpace {
        int size;
        int letterSpriteContained = -1;
        Point position = new Point();
    }

}
