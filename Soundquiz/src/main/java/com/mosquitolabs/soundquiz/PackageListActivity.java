package com.mosquitolabs.soundquiz;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

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

    private RelativeLayout layout;
    private View fadingOutBackground;
    private View fadingInBackground;

    private final static int FORWARD = 0;
    private final static int BACKWARD = 1;

    private boolean scrolling = false;
    private boolean isItemSet = false;
    private boolean isComingBack = false;
    private float previousOffset = 0.0f;
    private int width;
    private int height;
    private int fadingOutItem = 0;
    private int fadingInItem;
    private int PACKAGE_ITEM_WIDTH_PIXEL;
    private int PACKAGE_ITEM_HEIGHT_PIXEL;
    private int scrollingState = 0;
    private int direction = FORWARD;
    private int currentPage = 0;

    private ArrayList<Float> firstOffsets = new ArrayList<Float>();
    private final static int SAFETY_CHECKS = 2;

    private ArrayList<Drawable> images = new ArrayList<Drawable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fadingInBackground = findViewById(R.id.fadingInBackgroundView);
        fadingOutBackground = findViewById(R.id.fadingOutBackgroundView);

        images.add(getResources().getDrawable(R.drawable.theme1));
        images.add(getResources().getDrawable(R.drawable.theme2));
        images.add(getResources().getDrawable(R.drawable.theme3));

        pager = (VerticalViewPager) findViewById(R.id.packagePagerView);
        pagerAdapter = new PackagePagerAdapter(getSupportFragmentManager());

        pager.setOffscreenPageLimit(PackageCollection.getInstance().getPackageCollection().size());
        pager.setBackgroundColor(Color.TRANSPARENT);
        setPageMarginAndPackageSize();
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new BackgroundFadingTransformer(this));

        layout = (RelativeLayout) findViewById(R.id.packageRelativeLayout);
        layout.setBackgroundColor(Color.YELLOW);


//        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                fadeAnimation(position, positionOffset);
//                previousOffset = positionOffset;
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Log.d("PAGE", Integer.toString(position));
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                scrollingState = state;
//                if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    endFadeAnimation();
//                }
//            }
//        });


        getActionBar().setTitle("SoundQuiz");
        getActionBar().setDisplayHomeAsUpEnabled(true);


        setBackgroundImage(fadingOutBackground, 0);
        setBackgroundImage(fadingInBackground, 1);
//        fadingOutBackground.setBackgroundColor(themes[0]);
//        fadingInBackground.setBackgroundColor(themes[1]);



        fadingOutBackground.getBackground().setAlpha(255);
        fadingInBackground.getBackground().setAlpha(0);

    }

    private void setPageMarginAndPackageSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

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

    private void endFadeAnimation() {
        currentPage = pager.getCurrentItem();
        if (scrolling) {
            if (pager.getCurrentItem() == fadingOutItem) {
                fadingOutBackground.getBackground().setAlpha(255);
                fadingInBackground.getBackground().setAlpha(0);
            } else {
                fadingOutBackground.getBackground().setAlpha(0);
                fadingInBackground.getBackground().setAlpha(255);
            }
        }
        scrolling = false;


        Log.d("END", " Page: " + pager.getCurrentItem());

    }

    private void fadeAnimation(int position, float positionOffset) {
        if (positionOffset == 0.0f) {
            Log.d("ZERO", " " + position);
        } else {
            if (!scrolling) {
                if (firstOffsets.size() + 1 > SAFETY_CHECKS) {
                    boolean forward = true;
                    boolean backward = true;

                    for (float offset : firstOffsets) {
                        if (offset < 0.5) {
                            backward = false;
                            break;
                        }
                    }
                    for (float offset : firstOffsets) {
                        if (offset > 0.5) {
                            forward = false;
                            break;
                        }
                    }
                    boolean proceed = (backward || forward);
                    if (proceed) {

                        if (positionOffset > 0.5f) {
                            direction = BACKWARD;
                            if (!isItemSet) {
//                                fadingOutItem = pager.getCurrentItem();
                                fadingOutItem = currentPage;
                                fadingInItem = fadingOutItem - 1;
                            }
                            previousOffset = 1.0f;
                        } else {
                            direction = FORWARD;
                            if (!isItemSet) {
//                                fadingOutItem = pager.getCurrentItem();
                                fadingOutItem = currentPage;
                                fadingInItem = fadingOutItem + 1;
                            }
                            previousOffset = 0.0f;
                        }
//                    isItemSet = false;
//                    try {
//                        fadingOutBackground.setBackgroundColor(themes[fadingOutItem]);
//                        fadingInBackground.setBackgroundColor(themes[fadingInItem]);
//                        fadingOutBackground.getBackground().setAlpha(255);
//                        fadingInBackground.getBackground().setAlpha(0);
//                    } catch (Exception e) {
//                    }
//
//                } else {
//                    direction = FORWARD;
//
//                    if (!isItemSet) {
//                        fadingOutItem = pager.getCurrentItem();
//                        fadingInItem = fadingOutItem + 1;
//                    }
//                    isItemSet = false;
//
//                    try {
//                        fadingOutBackground.setBackgroundColor(themes[fadingOutItem]);
//                        fadingInBackground.setBackgroundColor(themes[fadingInItem]);
//                        fadingOutBackground.getBackground().setAlpha(255);
//                        fadingInBackground.getBackground().setAlpha(0);
//                    } catch (Exception e) {
//                    }
//                }

                        if (isItemSet) {
                            if (fadingOutItem > fadingInItem) {
                                direction = BACKWARD;
                                previousOffset = 1.0f;
                            } else {
                                direction = FORWARD;
                                previousOffset = 0.0f;
                            }
                            isItemSet = false;
                        }

                        try {
                            setBackgroundImage(fadingOutBackground, fadingOutItem);
                            setBackgroundImage(fadingInBackground, fadingInItem);
                            fadingOutBackground.getBackground().setAlpha(255);
                            fadingInBackground.getBackground().setAlpha(0);
                        } catch (Exception e) {
                        }

                        Log.d("FADE", "start: " + fadingOutItem + " end: " + fadingInItem);
                        scrolling = true;
                    } else {
                        Log.d("CONTRADICTION", "offsets:  " + firstOffsets);
                    }
                    firstOffsets.clear();
                } else {
                    firstOffsets.add(positionOffset);
                }

            } else {
                switch (direction) {
                    case FORWARD:
                        forwardFadingAnimationLogic(positionOffset);
                        break;
                    case BACKWARD:
                        backwardFadingAnimationLogic(positionOffset);
                        break;
                }
            }
        }
    }

    private void resetAnimationByFadingPages(int fadingInItem, int fadingOutItem) {
        this.fadingInItem = fadingInItem;
        this.fadingOutItem = fadingOutItem;
        setBackgroundImage(fadingOutBackground, fadingOutItem);
        setBackgroundImage(fadingInBackground, fadingInItem);
//        fadingOutBackground.setBackgroundColor(themes[fadingOutItem]);
//        fadingInBackground.setBackgroundColor(themes[fadingInItem]);
        fadingOutBackground.getBackground().setAlpha(255);
        fadingInBackground.getBackground().setAlpha(0);
        if (fadingOutItem > fadingInItem) {
            direction = BACKWARD;
            previousOffset = 1.0f;
        } else {
            direction = FORWARD;
            previousOffset = 0.0f;
        }
        isComingBack = false;

        Log.d("RESET", "start: " + fadingInItem + " end: " + fadingOutItem);

    }

    private void backwardFadingAnimationLogic(float positionOffset) {
        if (positionOffset != 0.0f) {
            if (scrollingState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                backwardFadingAnimation(positionOffset);
            } else if (scrollingState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                if (positionOffset < previousOffset) {
                    if (isComingBack && (previousOffset - positionOffset) > 0.5f) {
//                    We changed current page! (FORWARD)
                        if (fadingOutItem < PackageCollection.getInstance().getPackageCollection().size() - 1)
                            resetAnimationByFadingPages(fadingOutItem + 1, fadingOutItem);
                    } else {
//                    all fine
                        backwardFadingAnimation(positionOffset);
                    }
                } else {
                    if (isComingBack) {
//                    all fine
                        backwardFadingAnimation(positionOffset);
                    } else if ((positionOffset - previousOffset) > 0.5f) {
//                    We changed current page! (BACKWARD)
                        if (fadingInItem > 0)
                            resetAnimationByFadingPages(fadingInItem - 1, fadingInItem);
                    }
                }
            }

            isComingBack = (positionOffset > previousOffset);
        }
    }

    private void forwardFadingAnimationLogic(float positionOffset) {
        if (positionOffset != 0.0f) {
            if (scrollingState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                forwardFadingAnimation(positionOffset);
            } else if (scrollingState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                if (positionOffset > previousOffset) {
                    if (isComingBack && (positionOffset - previousOffset) > 0.5f) {
//                    We changed current page! (BACKWARD)
                        if (fadingOutItem > 0)
                            resetAnimationByFadingPages(fadingOutItem - 1, fadingOutItem);
                    } else {
//                    all fine
                        forwardFadingAnimation(positionOffset);
                    }
                } else {
                    if (isComingBack) {
//                    all fine
                        forwardFadingAnimation(positionOffset);
                    } else if ((previousOffset - positionOffset) > 0.5f) {
//                    We changed current page! (FORWARD)
                        if (fadingInItem < PackageCollection.getInstance().getPackageCollection().size() - 1)
                            resetAnimationByFadingPages(fadingInItem + 1, fadingInItem);
                    }
                }
            }

            isComingBack = (positionOffset < previousOffset);
        }
    }

    private void backwardFadingAnimation(float positionOffset) {
        if (!(positionOffset > 0.8 && previousOffset < 0.2) || scrollingState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            int fadingOutAlpha = (int) (positionOffset * 255);
            int fadingInAlpha = 255 - fadingOutAlpha;
            fadingInBackground.getBackground().setAlpha(fadingInAlpha);
            fadingOutBackground.getBackground().setAlpha(fadingOutAlpha);
            Log.d("SCROLL B", Float.toString(positionOffset));
        }
    }

    private void forwardFadingAnimation(float positionOffset) {
        if (!(positionOffset > 0.8 && previousOffset < 0.2) || scrollingState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            int fadingInAlpha = (int) (positionOffset * 255);
            int fadingOutAlpha = 255 - fadingInAlpha;
            fadingInBackground.getBackground().setAlpha(fadingInAlpha);
            fadingOutBackground.getBackground().setAlpha(fadingOutAlpha);
            Log.d("SCROLL F", Float.toString(positionOffset));
        }
    }

    public void setStartAndEndPages(int start, int end) {
        isItemSet = true;
        fadingOutItem = start;
        fadingInItem = end;
        Log.d("RESET", "setCurrentItem()  " + "start: " + fadingOutItem + " end: " + fadingInItem);

    }

    @TargetApi(19)
    public void setBackgroundImage(View view, int index) {
        int newIndex = index % 3;
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(images.get(newIndex));
        } else {
            view.setBackground(images.get(newIndex));
        }
    }

    public void setBackgroundColor(View view, int index) {
        view.setBackgroundColor(themes[index]);
    }


    public void setAlpha(View view, int alpha) {
        view.getBackground().setAlpha(alpha);
    }

    public View getFadingOutBackground() {
        return fadingOutBackground;
    }

    public View getFadingInBackground() {
        return fadingInBackground;
    }

    public int getScrollingState() {
        return scrollingState;
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
