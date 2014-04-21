package com.mosquitolabs.soundquiz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mosquitolabs.soundquiz.visualizer.AudioPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameView extends View {
    private final static int ANIMATION_TIME = 300; // (millis)
    private final static int MAX_DEGREE = 5; // (millis)

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

    private boolean firstDraw = true;
    private boolean gameMode = true;
    private boolean revealOneLetterMode = false;
    private boolean isRevealing = false;

    private float degree = 0;
    private float deltaDegree = 1.5f;

    private String answer;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (QuizActivity) context;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (firstDraw) {
            firstDraw = false;
            init(context.getQuizData());
        }


        if (gameMode) {
            for (LetterSpace letterSpace : letterSpaces) {
                canvas.drawRoundRect(new RectF(new Rect(letterSpace.position.x, letterSpace.position.y, letterSpace.position.x + letterSpace.size, letterSpace.position.y + letterSpace.size)), letterSpace.size / 15, letterSpace.size / 15, rectPaint);
            }

            for (int i = letterSprites.size() - 1; i >= 0; i--) {
                if (!context.getQuizData().isSolved() || !letterSprites.get(i).isHome()) {
                    letterSprites.get(i).onDraw(canvas);
                }
            }
            drawCorrectLines(canvas);

        } else if (revealOneLetterMode) {
            // draw fixed letters
            for (LetterSprite letterSprite : letterSprites) {
                if (letterSprite.isFix()) {
                    letterSprite.onDraw(canvas);
                }
            }

            for (LetterSpace letterSpace : letterSpaces) {
                if (letterSpace.letterSpriteContained == -1) {
                    canvas.drawRoundRect(new RectF(new Rect(letterSpace.position.x, letterSpace.position.y, letterSpace.position.x + letterSpace.size, letterSpace.position.y + letterSpace.size)), letterSpace.size / 15, letterSpace.size / 15, rectPaint);
                }
            }

        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (gameMode && !context.getQuizData().isSolved()) {
            for (LetterSprite letterSprite : letterSprites) {
                letterSprite.checkCollision(event.getX(), event.getY());
            }
        } else if (revealOneLetterMode && !isRevealing) {
            isRevealing = true;
            int counter = 0;
            for (LetterSpace letterSpace : letterSpaces) {
                if (letterSpace.letterSpriteContained == -1 && letterSpace.isCollision(event.getX(), event.getY())) {
                    String letter = String.valueOf(answer.charAt(counter));
                    for (LetterSprite letterSprite : letterSprites) {
                        if (letterSprite.isHome() && letterSprite.getLetter().equals(letter)) {
                            letterSprite.setPosition(letterSpace.position.x, letterSpace.position.y);
                            letterSpace.letterSpriteContained = letterSprite.getIndex();
                            letterSprite.setFix();
                            context.changeToGameMode();
                            isRevealing = false;
                            return super.onTouchEvent(event);
                        }
                    }
                }
                counter++;
            }
        }
        return super.onTouchEvent(event);
    }

    private void updateRotation() {
        degree += deltaDegree;
        if (degree >= MAX_DEGREE || degree <= -MAX_DEGREE) {
            deltaDegree *= -1;
        }
    }

    public void refresh() {
        invalidate();
    }


    public void init(final QuizData quizData) {
        String answer = (context).getQuizData().getAnswer();
        this.answer = answer.toLowerCase().replace(" ", "").replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");

        rectPaint.setAntiAlias(true);

        initColors();
        initGrids(quizData);
        initLetterSprites();
        initLetterSpaces(quizData);

        if (context.getQuizData().isSolved()) {
            winTheGame();
            context.initWinGraphics();
        }
    }

    private void initColors() {
        switch (context.getPackageIndex()) {
            case Utility.CINEMA:
                rectPaint.setColor(getResources().getColor(R.color.space_cinema));
                break;
            case Utility.MUSIC:
                rectPaint.setColor(getResources().getColor(R.color.space_music));
                break;
            case Utility.VIP:
                rectPaint.setColor(getResources().getColor(R.color.space_character));
                rectPaint.setColor(Color.BLACK);
                rectPaint.setAlpha(140);
                break;
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
        if (spriteFullSize < getWidth() / 17 || spriteFullSize < Utility.convertDpToPixels(getActivityContext(), 35)) {
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
        spaceFullSize = Math.min(spaceFullSize, Utility.getWidth(getActivityContext()) / 9);

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


    private void drawCorrectLines(Canvas canvas) {
        if (context.getQuizData().isSolved()) {
            return;
        }

        int counter = 0;
        for (String row : context.getQuizData().getRows()) {
            row = row.replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");
            String[] words = row.toLowerCase().split(" ");

            ArrayList<Integer> indexes = new ArrayList<Integer>();
            for (String word : words) {
                indexes.clear();
                boolean isCorrect = true;
                for (int i = 0; i < word.length(); i++) {
                    String letter = String.valueOf(word.charAt(i));
                    if (letterSpaces.get(counter).letterSpriteContained == -1 || !letterSprites.get(letterSpaces.get(counter).letterSpriteContained).getLetter().equals(letter)) {
                        isCorrect = false;
                    }
                    indexes.add(counter);
                    counter++;
                }

                if (isCorrect) {
                    LetterSpace firstLetterSpace = letterSpaces.get(indexes.get(0));
                    LetterSpace lastLetterSpace = letterSpaces.get(indexes.get(indexes.size() - 1));
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setColor(Color.rgb(34, 139, 34));
                    canvas.drawRect(firstLetterSpace.position.x, firstLetterSpace.position.y + firstLetterSpace.size + 5, lastLetterSpace.position.x + lastLetterSpace.size, lastLetterSpace.position.y + lastLetterSpace.size + 8, paint);
                }
            }
        }
    }


    public void changeToRevealOneLetterMode() {
        for (LetterSprite letterSprite : letterSprites) {
            if (!letterSprite.isFix()) {
                letterSprite.reset();
            }
        }
        gameMode = false;
        revealOneLetterMode = true;
        degree = 0;
    }

    public void changeToGameMode() {
        gameMode = true;
        revealOneLetterMode = false;
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

    public int getMaxY() {
        return maxY;
    }

    public void checkAnswer() {
        String userAnswer = "";
        for (int i = 0; i < getLetterSpaces().size(); i++) {
            if (getLetterSpaces().get(i).letterSpriteContained < 0) {
                return;
            } else {
                userAnswer += getLetterSprites().get(getLetterSpaces().get(i).letterSpriteContained).getLetter();
            }
        }

        if (getAnswer().equals(userAnswer)) {
            AudioPlayer.getIstance().player.stop();
            AudioPlayer.getIstance().playWin();
            resetSpritesColor();
            getActivityContext().initWinPage();
        } else {
            AudioPlayer.getIstance().playWrong();
        }
    }

    public void winTheGame() {
        for (int i = 0; i < letterSpaces.size(); i++) {
            letterSpaces.get(i).letterSpriteContained = -1;
            String letter = String.valueOf(answer.charAt(i));
            for (LetterSprite letterSprite : letterSprites) {
                if (letterSprite.isHome() && letterSprite.getLetter().equals(letter)) {
                    letterSprite.goToFirstEmpty();
                    break;
                }
            }
        }

        for (LetterSprite letterSprite : letterSprites) {
            letterSprite.setClickable(false);
            letterSprite.setVisible(!letterSprite.isHome());
            letterSprite.resetColors();
        }
    }

    public void revealFirstLetters() {

        for (LetterSprite letterSprite : letterSprites) {
            letterSprite.reset();
        }

        int counter = 0;
        for (String row : context.getQuizData().getRows()) {
            row = row.replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");
            String[] words = row.toLowerCase().split(" ");

            for (String word : words) {
                for (LetterSprite letterSprite : letterSprites) {
                    if (letterSprite.isHome() && letterSprite.getLetter().equals(String.valueOf(word.charAt(0)))) {
                        letterSprite.setPosition(letterSpaces.get(counter).position.x, letterSpaces.get(counter).position.y);
                        letterSpaces.get(counter).letterSpriteContained = letterSprite.getIndex();
                        letterSprite.setFix();
                        break;
                    }
                }
                counter += word.length();
            }
        }
    }

    public void removeWrongLetters() {

        for (LetterSprite letterSprite : letterSprites) {
            letterSprite.reset();
        }


        ArrayList<Integer> indexes = new ArrayList<Integer>();

        for (String row : context.getQuizData().getRows()) {
            row = row.replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");
            String[] words = row.toLowerCase().split(" ");

            for (String word : words) {
                for (int i = 0; i < word.length(); i++) {
                    for (int z = 0; z < letterSprites.size(); z++) {
                        if (!indexes.contains(z)) {
                            if (letterSprites.get(z).getLetter().equals(String.valueOf(word.charAt(i)))) {
                                indexes.add(z);
                                break;
                            }
                        }
                    }
                }
            }
        }

        int counter = 0;
        for (int z = 0; z < letterSprites.size(); z++) {
            if (!indexes.contains(z) && counter < 6) {
                letterSprites.get(z).setVisible(false);
                counter++;
            }
        }
    }

    public void resetSprites() {
        for (LetterSprite letterSprite : letterSprites)
            letterSprite.reset();
    }

    public void resetSpritesColor() {
        for (LetterSprite letterSprite : letterSprites)
            letterSprite.resetColors();
    }

    public boolean isInRevalOneLetterMode() {
        return revealOneLetterMode;
    }


    static class LetterSpace {
        int size;
        int letterSpriteContained = -1;
        Point position = new Point();

        public boolean isCollision(float x2, float y2) {
            return x2 > position.x && x2 < position.x + size && y2 > position.y && y2 < position.y + size;
        }

    }


}
