package com.mosquitolabs.soundquiz.visualizer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View {

    private Activity context;
    private Paint paint;
    private Path path = new Path();

    private MyPoint shooter = null;

    private int curveHeight = 0;
    private int translation = 0;
    boolean growing = false;
    boolean growingTranslation = false;

    private Point[] vertexes = {null, null, null};


    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        this.context = (Activity) context;
        paint = new Paint() {{
            setStyle(Paint.Style.STROKE);
            setAntiAlias(true);
            setStrokeWidth(1.5f);
            setColor(Color.BLUE); // Line color
        }};
    }

    // called to inflate XML
    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context) {
        this(context, null, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (shooter == null) {
            shooter = new MyPoint();
            shooter.x = 50;
            shooter.y = 0;
            shooter.growing = true;
        }

        updateShooter();

        for (int i = 0; i < 3; i++) {
            if ((vertexes[i].y > getHeight() / 2 && vertexes[i].x < getWidth() / 2) || (vertexes[i].y < getHeight() / 2 && vertexes[i].x > getWidth() / 2)) {
                vertexes[i].y += 10;
            } else {
                vertexes[i].y -= 10;
            }

            if (vertexes[i].x < getWidth() - 10) {
                vertexes[i].x += 10;
            } else {
                vertexes[i].x = 0;
            }

        }


        if (growing) {
            curveHeight += 20;
            if (curveHeight >= getHeight()) {
                curveHeight = getHeight();
                growing = false;
            }
        } else {
            curveHeight -= 20;
            if (curveHeight <= 0) {
                curveHeight = 0;
                growing = true;
            }
        }

        if (growingTranslation) {
            translation += 4;
            if (translation >= getWidth() / 10) {
                translation = getWidth() / 10;
                growingTranslation = false;
            }
        } else {
            translation -= 4;
            if (translation <= -getWidth() / 10) {
                translation = -getWidth() / 10;
                growingTranslation = true;
            }
        }


//        translation = curveHeight / 4;

        path.reset();

        Point start = new Point(0, getHeight() / 2);
        Point end = new Point(getWidth(), getHeight() / 2);

        Point point1 = new Point(getWidth() / 8 + translation, getHeight() / 4 + curveHeight / 2);
        Point point2 = new Point(getWidth() / 4 + translation, getHeight() / 2);
        Point point3 = new Point(getWidth() / 2 + translation, getHeight() - curveHeight);
        Point point4 = new Point(getWidth() * 3 / 4 + translation, getHeight() / 2);
        Point point5 = new Point(getWidth() * 7 / 8 + translation, getHeight() / 4 + curveHeight / 2);


        path.moveTo(start.x, start.y);
//        path.quadTo(start.x, start.y, start.x + 10, start.y);

        path.cubicTo(start.x, start.y, point1.x, point1.y, point2.x, point2.y);
        path.cubicTo(point2.x, point2.y, point3.x, point3.y, point4.x, point4.y);
        path.cubicTo(point4.x, point4.y, point5.x, point5.y, end.x, end.y);

//        path.quadTo(end.x - 10, end.y, end.x, end.y);


        canvas.drawPath(path, paint);
        invalidate();

    }


    public void refresh() {
        invalidate();
    }


    private void updateShooter() {
        if (shooter.growing) {
            shooter.y += 5;
            if (shooter.y >= 50) {
                shooter.y = 50;
                shooter.growing = false;
            }
        } else {
            shooter.y -= 5;
            if (shooter.y <= -50) {
                shooter.y = -50;
                shooter.growing = true;
            }
        }
    }


    private static class MyPoint {
        int x;
        int y;
        boolean growing;
    }

}
