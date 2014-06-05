package com.mosquitolabs.soundquiz;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

public class PackageListActivity extends FragmentActivity {

    private VerticalViewPager pager;
    private PackagePagerAdapter pagerAdapter;

    private int PACKAGE_ITEM_WIDTH_PIXEL;
    private int PACKAGE_ITEM_HEIGHT_PIXEL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);

        findViewById(R.id.back).setAlpha(0.5f);
        findViewById(R.id.back).getLayoutParams().height = Utility.convertDpToPixels(this, 50) * 2 / 3;
        findViewById(R.id.back).getLayoutParams().width = (int) (Utility.convertDpToPixels(this, 50) * 0.666f * 2.25f);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pager = (VerticalViewPager) findViewById(R.id.packagePagerView);
        pagerAdapter = new PackagePagerAdapter(getSupportFragmentManager());

        pager.setOffscreenPageLimit(PackageCollection.getInstance().getPackageCollection().size());
        pager.setBackgroundColor(Color.TRANSPARENT);
        setPageMarginAndPackageSize();
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new BackgroundFadingTransformer(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPager();
        updateLabels();
    }

    private void refreshPager() {
        pagerAdapter.notifyDataSetChanged();
    }

    private void updateLabels() {
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                ((PackageFragment) fragment).updateProgress();
            }
        } catch (Exception e) {
        }
    }

    private void setPageMarginAndPackageSize() {
        int width = Utility.getWidth(this);
        int height = Utility.getHeight(this);

        PACKAGE_ITEM_WIDTH_PIXEL = width * 4 / 5;
        PACKAGE_ITEM_HEIGHT_PIXEL = PACKAGE_ITEM_WIDTH_PIXEL / 2;

        int marginX = (height - PACKAGE_ITEM_HEIGHT_PIXEL) / 2;
        int pageMargin = -(marginX + PACKAGE_ITEM_HEIGHT_PIXEL / 4);

        pager.setPageMargin(pageMargin);
    }

    public int getPackageWidth() {
        return PACKAGE_ITEM_WIDTH_PIXEL;
    }

    public int getPackageHeight() {
        return PACKAGE_ITEM_HEIGHT_PIXEL;
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
