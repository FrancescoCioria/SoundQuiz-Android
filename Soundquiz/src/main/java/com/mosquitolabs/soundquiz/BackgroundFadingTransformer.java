package com.mosquitolabs.soundquiz;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class BackgroundFadingTransformer implements ViewPager.PageTransformer {
    private static float SCALE_RATE = 0.25f;
    private PackageListActivity context;
    private float DISTANCE = 0.0f;

    private View view1 = null;
    private View view2 = null;

    private View[] views = {view1, view2};

    public BackgroundFadingTransformer(Context context) {
        this.context = (PackageListActivity) context;
        views[0] = this.context.getFadingOutBackground();
        views[1] = this.context.getFadingInBackground();
    }

    public void transformPage(View view, float position) {
        TextView name = (TextView) ((ViewGroup) ((ViewGroup) view).getChildAt(0)).getChildAt(1);
        int index = Integer.parseInt(name.getText().toString());

//        INIT DISTANCE BETWEEN PAGES
        if (DISTANCE == 0.0f && index == 1) {
            DISTANCE = position;
        }

        float absPosition = Math.abs(position);
        float offset = (absPosition / DISTANCE);

//        CHECK IF WE ARE IN THE VISIBLE AREA
        if (absPosition < DISTANCE) {
            int alpha = (int) ((1 - offset) * 255);

            if (position <= 0) {
                context.setBackgroundImage(views[0], index);
                context.setAlpha(views[0], alpha);
            } else {
                context.setBackgroundImage(views[1], index);
                context.setAlpha(views[1], alpha);
            }
        }

        if (absPosition <= (DISTANCE + 0.1f) && DISTANCE > 0) {
            // Scale the page down (between (1-SCALE_RATE) and 1)
            float scaleFactor = 1 - (offset * SCALE_RATE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }

}




