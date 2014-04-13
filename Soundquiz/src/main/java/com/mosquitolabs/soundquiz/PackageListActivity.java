package com.mosquitolabs.soundquiz;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class PackageListActivity extends FragmentActivity {

//    private final static int RED = Color.rgb(190, 0, 0);
//    private final static int GREEN = Color.rgb(0, 160, 0);
//    private final static int BLUE = Color.rgb(11, 97, 164);
//    private final static int ORANGE = Color.rgb(255, 146, 0);
//    private final static int PURPLE = Color.rgb(159, 62, 213);
//    private final static int YELLOW = Color.rgb(255, 200, 0);
//
//    private final int[] themes = {RED, GREEN, BLUE, YELLOW, Color.MAGENTA, Color.CYAN};

    private VerticalViewPager pager;
    private PackagePagerAdapter pagerAdapter;


    private int PACKAGE_ITEM_WIDTH_PIXEL;
    private int PACKAGE_ITEM_HEIGHT_PIXEL;

    private ArrayList<Drawable> images = new ArrayList<Drawable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_list_new);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);

//        Button buttonBack = (Button) findViewById(R.id.buttonBack);
//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        for (int i = 0; i < PackageCollection.getInstance().getPackageCollection().size(); i += 2) {
//            images.add(getResources().getDrawable(R.drawable.simpsons_background));
//            images.add(getResources().getDrawable(R.drawable.tour_eiffel));
//        }
//
//
//        if (PackageCollection.getInstance().getPackageCollection().size() == 1) {
//            ImageView image = (ImageView) findViewById(R.id.firstView);
//            ImageView image2 = (ImageView) findViewById(R.id.secondView);
//            setBackgroundImage(image, 0);
//            setAlpha(image, 1f);
//            setAlpha(image2, 0f);
//        }

        pager = (VerticalViewPager) findViewById(R.id.packagePagerView);
        pagerAdapter = new PackagePagerAdapter(getSupportFragmentManager());

        pager.setOffscreenPageLimit(PackageCollection.getInstance().getPackageCollection().size());
        pager.setBackgroundColor(Color.TRANSPARENT);
        setPageMarginAndPackageSize();
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new BackgroundFadingTransformer(this));

        int layoutWidth = (int) (Utility.getWidth(this) * 0.65f);
        findViewById(R.id.bodyOne).getLayoutParams().width = layoutWidth;


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPager();
    }

    private void refreshPager() {
        pagerAdapter.notifyDataSetChanged();
    }

    private void setPageMarginAndPackageSize() {
        int width = Utility.getWidth(this);
        int height = Utility.getHeight(this);

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

//    public void setBackgroundColor(ImageView view, int index) {
//        view.setBackgroundColor(themes[index]);
//    }

    @TargetApi(11)
    public void setAlpha(ImageView image, float alpha) {
        image.setAlpha(alpha);
    }

    public void setAlpha(ImageView image, int alpha) {
        image.setAlpha(alpha);
    }


    public VerticalViewPager getPager() {
        return pager;
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
