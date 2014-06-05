package com.mosquitolabs.soundquiz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by francesco on 3/10/14.
 */

public class QuizListViewAdapter extends BaseAdapter {

    private LevelActivity context;
    private int levelIndex;
    private int packageIndex;
    private LayoutInflater inflater;
    private PackageCollection packageCollection = PackageCollection.getInstance();


    private ArrayList<QuizImage> drawables = new ArrayList<QuizImage>();


    private final int COLUMNS = 3;

    public QuizListViewAdapter(LevelActivity paramContext, int packIndex, int levelIndex) {
        this.inflater = LayoutInflater.from(paramContext);
        this.context = paramContext;
        this.levelIndex = levelIndex;
        this.packageIndex = packIndex;
    }

    public int getCount() {
        int size = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().size();
        if (size % COLUMNS == 0) {
            return size / COLUMNS;
        }
        return (size / COLUMNS + 1);
    }

    public Object getItem(int paramInt) {
        return paramInt;
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    private void startQuizActivity(QuizData quizData, int paramInt) {
        Intent mIntent = new Intent(context, QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", quizData.getID());
        bundle.putString("category", packageCollection.getPackageCollection().get(packageIndex).getCategory());
        bundle.putInt("quizIndex", paramInt);
        bundle.putInt("levelIndex", levelIndex);
        bundle.putInt("packageIndex", packageIndex);
        mIntent.putExtras(bundle);

        context.startActivity(mIntent);
    }

    public View getView(final int paramInt, View paramView,
                        ViewGroup paramViewGroup) {

        QuizItemViewHolder quizItemViewHolder;

        if (paramView == null) {
            paramView = inflater.inflate(R.layout.quiz_list_item,
                    null);
            quizItemViewHolder = new QuizItemViewHolder();

            quizItemViewHolder.mainLayout = (LinearLayout) paramView.findViewById(R.id.mainLayout);

            for (int i = 0; i < COLUMNS; i++) {
                String imageId = "imageView" + (i + 1);
                int resImage = context.getResources().getIdentifier(imageId, "id", context.getPackageName());
                quizItemViewHolder.images[i] = (CircularImageView) paramView.findViewById(resImage);

                quizItemViewHolder.images[i].setBorderColor(Color.parseColor("#80ffffff"));
                int imageWidth = (Utility.getWidth(context) - Utility.convertDpToPixels(context, 30)) / 3;
                int imageHeight = packageIndex == Utility.CINEMA ? imageWidth * 3 / 2 : imageWidth;

                quizItemViewHolder.images[i].getLayoutParams().height = imageHeight;
                quizItemViewHolder.images[i].setBorderWidth(1);

                if (packageIndex == Utility.VIP) {
                    quizItemViewHolder.images[i].setCircularShape();
                } else {
                    quizItemViewHolder.images[i].setRectangularShape();
                }
            }
            paramView.setTag(quizItemViewHolder);
        } else {
            quizItemViewHolder = (QuizItemViewHolder) paramView.getTag();
        }

        for (int i = 0; i < COLUMNS; i++) {
            final int index = paramInt * COLUMNS + i;
            final QuizData quizData = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(index);
            quizItemViewHolder.images[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuizActivity(quizData, index);
                }
            });
            String path = quizData.getID();

            int res;
            if (quizData.isSolved()) {
                res = context.getResources().getIdentifier(path, "drawable", context.getPackageName());
            } else {
                res = context.getResources().getIdentifier(path + "_blur", "drawable", context.getPackageName());
            }

            Drawable image;
            if (drawables.size() > index && quizData.isSolved() == drawables.get(index).isSolved) {
                image = drawables.get(index).image;
            } else {
                image = context.getResources().getDrawable(res);
                if (drawables.size() > index) {
                    drawables.get(index).image = image;
                    drawables.get(index).isSolved = quizData.isSolved();
                } else {
                    drawables.add(index, initQuizImage(image, quizData.isSolved()));
                }
            }
            quizItemViewHolder.images[i].setImageDrawable(image);

        }


        if ((paramInt * COLUMNS + (COLUMNS - 1)) == packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().size() - 1) {
            Utility.setMargins(quizItemViewHolder.mainLayout, 0, 0, 0, Utility.convertDpToPixels(context, 10));
        } else {
            Utility.setMargins(quizItemViewHolder.mainLayout, 0, 0, 0, 0);
        }

        return paramView;
    }


    private QuizImage initQuizImage(Drawable image, boolean isSolved) {
        QuizImage quizImage = new QuizImage();
        quizImage.image = image;
        quizImage.isSolved = isSolved;

        return quizImage;
    }

    class QuizImage {
        Drawable image;
        boolean isSolved = false;
    }

    static class QuizItemViewHolder {
        CircularImageView images[] = {null, null, null};
        LinearLayout mainLayout;
    }


}