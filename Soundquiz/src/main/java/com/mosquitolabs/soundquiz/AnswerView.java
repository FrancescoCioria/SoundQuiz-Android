package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by francesco on 4/9/14.
 */
public class AnswerView extends View {

    private Paint rectPaint = new Paint();
    private Paint framePaint = new Paint();
    private Paint textPaint = new Paint();
    private Activity context;

    private ArrayList<Letter> letterViews = new ArrayList<Letter>();

    private boolean firstDraw = true;

    private int fullSize;

    RectF roundedFrame;

    private Runnable handlerTask;

    private String answer;


    public AnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        rectPaint.setColor(Color.rgb(217, 152, 110));
        rectPaint.setAntiAlias(true);
        framePaint.setColor(Color.rgb(117, 50, 0));
        framePaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (firstDraw) {
            firstDraw = false;
            fullSize = ((WinActivity) context).getSpaceFullSize();
            textPaint.setTextSize(fullSize * 9 / 20);
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.BLACK);
            textPaint.setStrokeWidth(3);
            init(((WinActivity) context).getQuizData());
        }

        canvas.drawRoundRect(roundedFrame, getWidth() / 40, getWidth() / 40, framePaint);

        for (Letter letterView : letterViews) {
            canvas.drawRoundRect(letterView.roundedRect, fullSize * 0.9f / 15, fullSize * 0.9f / 15, rectPaint);
            canvas.drawText(letterView.letter.toUpperCase(), letterView.roundedRect.centerX() - letterView.textWidth / 2f, letterView.roundedRect.centerY() + letterView.textHeight / 2f, textPaint);
        }


    }


    public void init(final QuizData quizData) {
        String answer = ((WinActivity) context).getQuizData().getAnswers().get(0);
        this.answer = answer.toLowerCase().replace(" ", "").replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");

        initLetters(quizData);
    }

    private void initLetters(QuizData quizData) {

        letterViews.clear();

        int size = fullSize * 90 / 100;
        int GAP = fullSize - size;
        int ROW_GAP = size / 5;

        int currentRow = 0;
        int marginY = 0;
        for (String row : quizData.getRows()) {
            marginY = Math.max(marginY, (int) ((row.replace(" ", "").length() * size + (row.length() - row.replace(" ", "").length()) * size / 2 + (row.length() - (row.length() - row.replace(" ", "").length()) * 2 - 1) * GAP) * 0.04f));
        }

        for (String row : quizData.getRows()) {
            row = row.replace(".", "").replace("-", "").replace("'", "").replace(",", "").replace(":", "").replace("!", "").replace("?", "");
            String[] words = row.split(" ");
            boolean firstWord = true;
            int marginX = (getWidth() - (row.replace(" ", "").length() * size + (row.length() - row.replace(" ", "").length()) * size / 2 + (row.length() - (row.length() - row.replace(" ", "").length()) * 2 - 1) * GAP)) / 2;

            for (String word : words) {
                for (int z = 0; z < word.length(); z++) {
                    Letter letter = new Letter();
                    int positionX;
                    if (z == 0) {
                        if (firstWord) {
                            firstWord = false;
                            positionX = marginX;
                        } else {
                            positionX = (int) (letterViews.get(letterViews.size() - 1).roundedRect.left + (size * 3 / 2));
                        }
                    } else {
                        positionX = (int) (letterViews.get(letterViews.size() - 1).roundedRect.left + (size + GAP));
                    }

                    int positionY = marginY + currentRow * (size + ROW_GAP);

                    letter.roundedRect = new RectF(new Rect(positionX, positionY, positionX + size, positionY + size));
                    letter.letter = String.valueOf(word.charAt(z));
                    letter.textWidth = textPaint.measureText(letter.letter.toUpperCase());
                    Rect bounds = new Rect();
                    textPaint.getTextBounds("T", 0, 1, bounds);
                    letter.textHeight = bounds.height();
                    letterViews.add(letter);
                }
            }
            currentRow++;
        }

        int minX = getWidth();
        int maxX = 0;
        int maxY = 0;
        for (Letter letter : letterViews) {
            minX = Math.min((int) letter.roundedRect.left, minX);
            maxX = Math.max((int) letter.roundedRect.left, maxX);
            maxY = Math.max((int) letter.roundedRect.top, maxY);
        }

        float over = marginY;

        int left = (int) (minX - over);
        int top = (int) (over);
        int right = (int) (maxX + size + over);
        int bottom = (int) (maxY + size + over);

        roundedFrame = new RectF(new Rect(left, 0, right, bottom));


    }


    public Context getActivityContext() {
        return context;
    }


    static class Letter {
        String letter = "";
        RectF roundedRect;
        float textWidth;
        float textHeight;
    }


}
