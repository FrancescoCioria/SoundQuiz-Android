package com.mosquitolabs.soundquiz;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class PackageListActivity extends FragmentActivity {

    private final static int RED = Color.rgb(190, 0, 0);
    private final static int GREEN = Color.rgb(0, 160, 0);
    private final static int BLUE = Color.rgb(11, 97, 164);
    private final static int ORANGE = Color.rgb(255, 146, 0);
    private final static int PURPLE = Color.rgb(159, 62, 213);
    private final static int YELLOW = Color.rgb(255, 200, 0);

    private final int[] themes = {RED, GREEN, BLUE, YELLOW, Color.MAGENTA, Color.CYAN};

    private VerticalViewPager pager;
    private PackagePagerAdapter pagerAdapter;

    private ImageView firstView;
    private ImageView secondView;

    private int width;
    private int height;
    private int PACKAGE_ITEM_WIDTH_PIXEL;
    private int PACKAGE_ITEM_HEIGHT_PIXEL;

    private ArrayList<Drawable> images = new ArrayList<Drawable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        images.add(getResources().getDrawable(R.drawable.sunset_blur));
        images.add(getResources().getDrawable(R.drawable.tour_eiffel));
        images.add(getResources().getDrawable(R.drawable.blue));
        images.add(getResources().getDrawable(R.drawable.green));
        images.add(getResources().getDrawable(R.drawable.gold));
        images.add(getResources().getDrawable(R.drawable.brown));

        pager = (VerticalViewPager) findViewById(R.id.packagePagerView);
        pagerAdapter = new PackagePagerAdapter(getSupportFragmentManager());

        pager.setOffscreenPageLimit(PackageCollection.getInstance().getPackageCollection().size());
        pager.setBackgroundColor(Color.TRANSPARENT);
        setPageMarginAndPackageSize();
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new BackgroundFadingTransformer(this));

        getActionBar().setTitle("SoundQuiz");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();


    }

    private void setPageMarginAndPackageSize() {
        width = Utility.getWidth(this);
        height = Utility.getHeight(this);

        PACKAGE_ITEM_WIDTH_PIXEL = width * 4 / 5;
        PACKAGE_ITEM_HEIGHT_PIXEL = PACKAGE_ITEM_WIDTH_PIXEL * 2 / 3;

        int marginX = (height - PACKAGE_ITEM_HEIGHT_PIXEL) / 2;
        int pageMargin = -(marginX + PACKAGE_ITEM_HEIGHT_PIXEL / 4);

        pager.setPageMargin(pageMargin);
    }

    public int getPackageWidth() {
        return PACKAGE_ITEM_WIDTH_PIXEL;
    }

    public int getPackageHeigth() {
        return PACKAGE_ITEM_HEIGHT_PIXEL;
    }


    public void setBackgroundImage(ImageView image, int index) {
        image.setImageDrawable(images.get(index));
    }

    public void setBackgroundColor(ImageView view, int index) {
        view.setBackgroundColor(themes[index]);
    }


    public void setAlpha(ImageView image, float alpha) {
        image.setAlpha(alpha);
    }


    public VerticalViewPager getPager() {
        return pager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.package_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private class PackagePagerAdapter extends FragmentStatePagerAdapter {
        public PackagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PackageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PackageCollection.getInstance().getPackageCollection().size();
        }
    }

}
