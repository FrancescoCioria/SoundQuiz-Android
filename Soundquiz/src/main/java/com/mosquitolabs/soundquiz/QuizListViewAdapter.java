package com.mosquitolabs.soundquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by francesco on 3/10/14.
 */
public class QuizListViewAdapter extends BaseAdapter {

    private LevelActivity context;
    private int levelIndex;
    private int packageIndex;
    private LayoutInflater inflater;
    private PackageCollection packageCollection = PackageCollection.getInstance();

    private int imageMinWidth = 0;

    private final int SUGGESTED_MAX_WIDTH = 250;
    private int SUGGESTED_MAX_SPACE = 20;
    private int MIN_SPACE = 5;

    public QuizListViewAdapter(LevelActivity paramContext, int packIndex, int levelIndex) {
        this.inflater = LayoutInflater.from(paramContext);
        this.context = paramContext;
        this.levelIndex = levelIndex;
        this.packageIndex = packIndex;
    }


    public int getCount() {
        int size = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().size();
        if (size % 4 == 0) {
            return size / 4;
        }
        return (size / 4 + 1);
    }

    public Object getItem(int paramInt) {
        return Integer.valueOf(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    private void startQuizActivity(QuizData quizData, int paramInt) {
        Intent mIntent = new Intent(context, QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("answer", quizData.getAnswer());
        bundle.putString("category", quizData.getCategroy());
        bundle.putInt("quizIndex", paramInt);
        bundle.putInt("levelIndex", levelIndex);
        bundle.putInt("packageIndex", packageIndex);
        mIntent.putExtras(bundle);

        context.startActivity(mIntent);
    }

    public View getView(final int paramInt, View paramView,
                        ViewGroup paramViewGroup) {

        QuizItemViewHolder quizItemViewHolder;

        final int paramInt1 = paramInt * 4;
        final QuizData quizData1 = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(paramInt1);
        final int paramInt2 = paramInt * 4 + 1;
        final QuizData quizData2 = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(paramInt2);
        final int paramInt3 = paramInt * 4 + 2;
        final QuizData quizData3 = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(paramInt3);
        final int paramInt4 = paramInt * 4 + 3;
        final QuizData quizData4 = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(paramInt4);


        if (paramView == null) {
            paramView = inflater.inflate(R.layout.quiz_list_item,
                    null);
            quizItemViewHolder = new QuizItemViewHolder();
            quizItemViewHolder.body1 = (TextView) paramView.findViewById(R.id.body1);
            quizItemViewHolder.body2 = (TextView) paramView.findViewById(R.id.body2);
            quizItemViewHolder.body3 = (TextView) paramView.findViewById(R.id.body3);
            quizItemViewHolder.body4 = (TextView) paramView.findViewById(R.id.body4);
            quizItemViewHolder.image1 = (ImageView) paramView.findViewById(R.id.imageView1);
            quizItemViewHolder.image2 = (ImageView) paramView.findViewById(R.id.imageView2);
            quizItemViewHolder.image3 = (ImageView) paramView.findViewById(R.id.imageView3);
            quizItemViewHolder.image4 = (ImageView) paramView.findViewById(R.id.imageView4);

            quizItemViewHolder.image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startQuizActivity(quizData1, paramInt1);
                }
            });
            quizItemViewHolder.image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuizActivity(quizData2, paramInt2);
                }
            });
            quizItemViewHolder.image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuizActivity(quizData3, paramInt3);
                }
            });
            quizItemViewHolder.image4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuizActivity(quizData4, paramInt4);
                }
            });


            paramView.setTag(quizItemViewHolder);
        } else {
            quizItemViewHolder = (QuizItemViewHolder) paramView.getTag();
        }

        if (quizData1.isSolved()) {
            quizItemViewHolder.body1.setText(quizData1.getAnswer());
        } else {
            quizItemViewHolder.body1.setText(Integer.toString(paramInt1 + 1) + "?");
        }
        if (quizData2.isSolved()) {
            quizItemViewHolder.body2.setText(quizData2.getAnswer());
        } else {
            quizItemViewHolder.body2.setText(Integer.toString(paramInt2 + 1) + "?");
        }
        if (quizData3.isSolved()) {
            quizItemViewHolder.body3.setText(quizData3.getAnswer());
        } else {
            quizItemViewHolder.body3.setText(Integer.toString(paramInt3 + 1) + "?");
        }
        if (quizData4.isSolved()) {
            quizItemViewHolder.body4.setText(quizData4.getAnswer());
        } else {
            quizItemViewHolder.body4.setText(Integer.toString(paramInt4 + 1) + "?");
        }


        return paramView;
    }

    static class QuizItemViewHolder {
        TextView body1;
        TextView body2;
        TextView body3;
        TextView body4;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        ImageView image4;
    }



}