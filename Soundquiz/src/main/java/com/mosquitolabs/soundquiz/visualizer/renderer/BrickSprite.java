package com.mosquitolabs.soundquiz.visualizer.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by francesco on 3/13/14.
 */
public class BrickSprite {

    private float left;
    private float top;
    private float right;
    private float bottom;

    private Paint paint = new Paint();
    private Paint paintWhite = new Paint();
    private ArrayList<Point> points = new ArrayList<Point>();
    private ArrayList<Point> pointsWhite = new ArrayList<Point>();

    public BrickSprite(float x, float y, float width, float height, int color) {
        this.left = x;
        this.top = y;
        this.right = width;
        this.bottom = height;
        float t = top;
        float m = top + (bottom - top) / 2;
        float b = bottom;
        float l = left;
        float ml = left + (right - left) / 15;
        float mr = right - (right - left) / 15;
        float r = right;

        Point[] points = {new Point(l, m), new Point(ml, t), new Point(mr, t), new Point(r, m), new Point(mr, b), new Point(ml, b)};
        Point[] pointsWhite = {new Point(r, m), new Point(mr, b), new Point(ml, b), new Point(l, m)};
        for (Point point : points) {
            this.points.add(point);
        }
        for (Point point : pointsWhite) {
            this.pointsWhite.add(point);
        }
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintWhite.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintWhite.setColor(Color.WHITE);
        paintWhite.setAlpha(100);

        setColor(color);
    }

    private void setColor(int color) {
        paint.setColor(color);
    }


    private void update() {
    }

    public void draw(Canvas canvas) {
        drawPoly(canvas, paint, points);
        drawPoly(canvas, paintWhite, pointsWhite);
//        canvas.drawRect(left, top, right, bottom, paint);
    }

    private void drawPoly(Canvas canvas, Paint paint, ArrayList<Point> points) {
        // line at minimum...
        if (points.size() < 2) {
            return;
        }

        // path
        Path polyPath = new Path();
        polyPath.moveTo(points.get(0).x, points.get(0).y);
        int i, len;
        len = points.size();
        for (i = 0; i < len; i++) {
            polyPath.lineTo(points.get(i).x, points.get(i).y);
        }
        polyPath.lineTo(points.get(0).x, points.get(0).y);


        // draw
        canvas.drawPath(polyPath, paint);
    }

    private class Point {

        public float x = 0;
        public float y = 0;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}


