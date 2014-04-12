package com.mosquitolabs.soundquiz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameView extends View {
    private final static int ANIMATION_TIME = 300; // (millis)

    private int spaceFullSize;
    private int spriteFullSize;
    private int spriteNumberOfRows;
    private int spriteNumber;
    private int spriteSpaceOccupied;
    private int maxY;
    private final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "y", "x", "z"};

    private Paint rectPaint = new Paint();
    private QuizActivity context;

    private ArrayList<LetterSpace> letterSpaces = new ArrayList<LetterSpace>();
    private ArrayList<LetterSprite> letterSprites = new ArrayList<LetterSprite>();

    private boolean stopGameView = true;
    private boolean firstDraw = true;

    private Runnable handlerTask;

    private String answer;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (QuizActivity) context;
//        rectPaint.setColor(Color.rgb(117, 50, 0));
        rectPaint.setColor(Color.rgb(0, 57, 40));
        rectPaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (firstDraw) {
            firstDraw = false;
            init(context.getQuizData());
        }

        for (LetterSpace letterSpace : letterSpaces) {
            canvas.drawRoundRect(new RectF(new Rect(letterSpace.position.x, letterSpace.position.y, letterSpace.position.x + letterSpace.size, letterSpace.position.y + letterSpace.size)), letterSpace.size / 15, letterSpace.size / 15, rectPaint);
        }

        for (int i = letterSprites.size() - 1; i >= 0; i--) {
            if (!context.getQuizData().isSolved() || !letterSprites.get(i).isHome()) {
                letterSprites.get(i).onDraw(canvas);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!context.getQuizData().isSolved()) {
            for (LetterSprite letterSprite : letterSprites) {
                letterSprite.checkCollision(event.getX(), event.getY());
            }
        }
        return super.onTouchEvent(event);
    }

    public void refresh() {
        invalidate();
    }


    public void init(final QuizData quizData) {
        String answer = (context).getQuizData().getAnswers().get(0);
        this.answer = answer.toLowerCase().replace(" ", "").replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");

        initGrids(quizData);
        initLetterSprites();
        initLetterSpaces(quizData);

        if (context.getQuizData().isSolved()) {
            winTheGame();
            context.initWinGraphics();
        }
    }

    private void initGrids(QuizData quizData) {
        //SPRITES
        spriteNumber = 12;
        spriteNumberOfRows = 2;

        if (answer.length() > 18) {
            spriteNumber = (answer.length() - answer.length() % 2) + 2;
        } else if (answer.length() > 14) {
            spriteNumber = 20;
        } else if (answer.length() > 10) {
            spriteNumber = 16;
        }

        spriteFullSize = (int) (0.95f * getWidth() / (spriteNumber / 2));
        if (spriteFullSize < getWidth() / 17 || spriteFullSize < Utility.convertDpToPixels(getActivityContext(), 40)) {
            while (spriteNumber % 3 != 0) {
                spriteNumber++;
            }
            spriteFullSize = (int) (0.95f * getWidth() / (spriteNumber / 3));
            spriteNumberOfRows = 3;
        }

        int maxSpaceSize = spriteFullSize - 2;

        //SPACES
        float maxItems = 0;
        float numberOfEmptySpaces = 0;
        for (String row : quizData.getRows()) {
            row = row.replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");
            String[] words = row.split(" ");

            int counter = 0;
            for (String word : words) {
                counter += word.length();
            }

            if (counter > maxItems) {
                maxItems = counter;
                numberOfEmptySpaces = row.length() - row.replace(" ", "").length();
            }
        }
        float percentage = 0.85f;

        if (maxItems > 12) {
            percentage = 0.95f;
        } else if (maxItems > 9) {
            percentage = 0.9f;
        }

        spaceFullSize = Math.min((int) ((percentage * getWidth() / (maxItems + numberOfEmptySpaces * 0.5f))), maxSpaceSize);
        spaceFullSize = Math.min(spaceFullSize, Utility.convertDpToPixels(getActivityContext(), 50));
        spaceFullSize = Math.min(spaceFullSize, Utility.getWidth(getActivityContext())/9);

    }

    private void initLetterSpaces(QuizData quizData) {
        letterSpaces.clear();
        int spaceSize = spaceFullSize * 90 / 100;
        int GAP = spaceFullSize - spaceSize;
        int ROW_GAP = spaceSize / 5;

        int currentRow = 0;
        int spaceOccupied = spaceSize * quizData.getRows().size() + ROW_GAP * (quizData.getRows().size() - 1);

        int marginY = (getHeight() - spriteSpaceOccupied - spaceOccupied) / 4;

        for (String row : quizData.getRows()) {
            row = row.replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");
            String[] words = row.split(" ");
            boolean firstWord = true;
            int marginX = (getWidth() - (row.replace(" ", "").length() * spaceSize + (row.length() - row.replace(" ", "").length()) * spaceSize / 2 + (row.length() - (row.length() - row.replace(" ", "").length()) * 2 - 1) * GAP)) / 2;


            for (String word : words) {
                for (int z = 0; z < word.length(); z++) {
                    LetterSpace letterSpace = new LetterSpace();
                    if (z == 0) {
                        if (firstWord) {
                            firstWord = false;
                            letterSpace.position.x = marginX;
                        } else {
                            letterSpace.position.x = letterSpaces.get(letterSpaces.size() - 1).position.x + (spaceSize * 3 / 2);
                        }
                    } else {
                        letterSpace.position.x = letterSpaces.get(letterSpaces.size() - 1).position.x + (spaceSize + GAP);
                    }


                    letterSpace.position.y = currentRow * (spaceSize + ROW_GAP) + marginY;
                    letterSpace.size = spaceSize;
                    letterSpaces.add(letterSpace);

                }
            }
            currentRow++;
        }

        maxY = 0;
        for (LetterSpace letterSpace : letterSpaces) {
            maxY = Math.max(letterSpace.position.y, maxY);
        }

        maxY += spaceSize;
    }

    private void initLetterSprites() {
        letterSprites.clear();
        ArrayList<String> letters = new ArrayList<String>();
        for (int i = 0; i < answer.length(); i++) {
            String letter = String.valueOf(answer.charAt(i));
            letters.add(letter);
        }

        int spaceSize = spaceFullSize * 90 / 100;

        int spriteSize = spriteFullSize * 90 / 100;
        int GAP = spriteFullSize - spriteSize;
        int ROW_GAP = spriteSize / 5;

        for (int z = answer.length(); z < spriteNumber; z++) {
            Random rnd = new Random();
            letters.add(alphabet[rnd.nextInt(alphabet.length)]);
        }
        Collections.shuffle(letters);

        int counter = 0;
        int currentRow = 0;
        int marginX = (getWidth() - (spriteNumber * spriteSize / spriteNumberOfRows + ((spriteNumber / spriteNumberOfRows) - 1) * GAP)) / 2;
        int marginY = marginX;
        spriteSpaceOccupied = marginY + spriteSize * (spriteNumberOfRows) + ROW_GAP * (spriteNumberOfRows - 1);

        for (String letter : letters) {
            int positionX = 0;
            if (counter != 0 && counter == spriteNumber / spriteNumberOfRows) {
                counter = 0;
                currentRow++;
            }

            if (counter == 0) {
                positionX += marginX;
            } else {
                positionX += letterSprites.get(letterSprites.size() - 1).getHome().x + (spriteSize + GAP);
            }
            int positionY = getHeight() - (marginY + spriteSize * (currentRow + 1) + ROW_GAP * currentRow);

            int index = counter + currentRow * (spriteNumber / spriteNumberOfRows);

            LetterSprite letterSprite = new LetterSprite(this, positionX, positionY, spriteSize, spaceSize, index, letter);
            letterSprites.add(letterSprite);
            counter++;
        }
    }


    public void startLoop() {
        if (stopGameView) {
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
    }

    public void stopLoop() {
        stopGameView = true;
    }

    public QuizActivity getActivityContext() {
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

    public int getSpaceFullSize() {
        return spaceFullSize;
    }

    public int getMaxY() {
        return maxY;
    }

    public void winTheGame() {
        for (int i = 0; i < letterSpaces.size(); i++) {
            letterSpaces.get(i).letterSpriteContained = -1;
            String letter = String.valueOf(answer.charAt(i));
            for (LetterSprite letterSprite : letterSprites) {
                if (letterSprite.isHome() && letterSprite.getLetter().equals(letter)) {
                    letterSprite.setPosition();
                    break;
                }
            }
        }
    }

    public void resetSprites() {
        for (LetterSprite letterSprite : letterSprites)
            letterSprite.reset();
    }

    static class LetterSpace {
        int size;
        int letterSpriteContained = -1;
        Point position = new Point();
    }


}
