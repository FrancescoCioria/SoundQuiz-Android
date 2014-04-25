package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by francesco on 3/14/14.
 */
public class LevelListViewAdapter extends BaseAdapter {


    private Activity context;
    private LayoutInflater inflater;
    private int packageIndex;
    private PackageCollection packageCollection = PackageCollection.getInstance();


    public LevelListViewAdapter(Activity paramContext, int packageIndex) {
        this.inflater = LayoutInflater.from(paramContext);
        this.context = paramContext;
        this.packageIndex = packageIndex;
    }

    public int getCount() {
        return packageCollection.getPackageCollection().get(packageIndex).getLevelList().size();
    }

    public Object getItem(int paramInt) {
        return Integer.valueOf(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }


    private void startLevelActivity(int paramInt) {
        Intent mIntent = new Intent(context, LevelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("levelIndex", paramInt);
        bundle.putInt("packageIndex", packageIndex);
        mIntent.putExtras(bundle);
        context.startActivity(mIntent);
    }


    public View getView(final int paramInt, View paramView,
                        ViewGroup paramViewGroup) {
        int solvedQuizzes = 0;
        int starsAchieved = 0;
        int totalSolvedQuizzes = 0;
        int i = 0;
        for (LevelData levelData : PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList()) {
            for (QuizData quizData : levelData.getQuizList()) {
                if (i == paramInt) {
                    solvedQuizzes += quizData.isSolved() ? 1 : 0;
                    starsAchieved += quizData.isSolvedWithStar() ? 1 : 0;
                }
                totalSolvedQuizzes += quizData.isSolved() ? 1 : 0;
            }
            i++;
        }


//        check if level is unlocked
        if (totalSolvedQuizzes >= paramInt * 10 || true) {
            UnlockedLevelItemViewHolder unlockedlevelItemViewHolder;

            try {
                unlockedlevelItemViewHolder = (UnlockedLevelItemViewHolder) paramView.getTag();
            } catch (Exception e) {
                paramView = inflater.inflate(R.layout.level_list_item_unlocked, null);
                unlockedlevelItemViewHolder = new UnlockedLevelItemViewHolder();

                unlockedlevelItemViewHolder.button = (Button) paramView.findViewById(R.id.playButton);
                unlockedlevelItemViewHolder.titleTextView = (TextView) paramView.findViewById(R.id.titleTextView);
                unlockedlevelItemViewHolder.percentageTextView = (TextView) paramView.findViewById(R.id.percentageTextView);
                unlockedlevelItemViewHolder.solvedQuizTextView = (TextView) paramView.findViewById(R.id.solvedQuizTextView);
                unlockedlevelItemViewHolder.starsAchievedTextView = (TextView) paramView.findViewById(R.id.starsAchievedTextView);
                unlockedlevelItemViewHolder.layout = (RelativeLayout) paramView.findViewById(R.id.layout);
                unlockedlevelItemViewHolder.progressBar = (ProgressBar) paramView.findViewById(R.id.progressBar);

                unlockedlevelItemViewHolder.layout.getLayoutParams().width = Utility.getWidth(context) * 93 / 100;

                paramView.setTag(unlockedlevelItemViewHolder);
            }

            unlockedlevelItemViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLevelActivity(paramInt);
                }
            });
            unlockedlevelItemViewHolder.titleTextView.setText("Level " + (paramInt + 1));
            unlockedlevelItemViewHolder.solvedQuizTextView.setText("solved: " + solvedQuizzes + " / " + 15);
            unlockedlevelItemViewHolder.starsAchievedTextView.setText("stars: " + starsAchieved);
            unlockedlevelItemViewHolder.percentageTextView.setText((solvedQuizzes * 100 / 15) + "%");
            unlockedlevelItemViewHolder.progressBar.setProgress(solvedQuizzes * 100 / 15);

            switch (packageIndex) {
                case Utility.CINEMA:
                    Utility.setBackgroundToView(unlockedlevelItemViewHolder.layout, context.getResources().getDrawable(R.drawable.level_rounded_layout_cinema));
                    break;
                case Utility.MUSIC:
                    Utility.setBackgroundToView(unlockedlevelItemViewHolder.layout, context.getResources().getDrawable(R.drawable.level_rounded_layout_music));
                    break;
                case Utility.VIP:
                    Utility.setBackgroundToView(unlockedlevelItemViewHolder.layout, context.getResources().getDrawable(R.drawable.level_rounded_layout_character));
                    break;
            }

            return paramView;

        }

        LockedLevelItemViewHolder lockedlevelItemViewHolder;

        if (paramView == null) {
            paramView = inflater.inflate(R.layout.level_list_item_locked, null);
            lockedlevelItemViewHolder = new LockedLevelItemViewHolder();

            lockedlevelItemViewHolder.layout = (RelativeLayout) paramView.findViewById(R.id.layout);
            lockedlevelItemViewHolder.titleTextView = (TextView) paramView.findViewById(R.id.titleTextView);
            lockedlevelItemViewHolder.toUnlockTextView = (TextView) paramView.findViewById(R.id.toUnlockTextView);

            lockedlevelItemViewHolder.layout.getLayoutParams().width = Utility.getWidth(context) * 93 / 100;

            paramView.setTag(lockedlevelItemViewHolder);
        } else {
            lockedlevelItemViewHolder = (LockedLevelItemViewHolder) paramView.getTag();
        }

        lockedlevelItemViewHolder.titleTextView.setText("Level " + (paramInt + 1));
        lockedlevelItemViewHolder.toUnlockTextView.setText("Solve " + (paramInt * 10 - totalSolvedQuizzes) + " quizzes to unlock");

        switch (packageIndex) {
            case Utility.CINEMA:
                Utility.setBackgroundToView(lockedlevelItemViewHolder.layout, context.getResources().getDrawable(R.drawable.level_rounded_layout_cinema));
                break;
            case Utility.MUSIC:
                Utility.setBackgroundToView(lockedlevelItemViewHolder.layout, context.getResources().getDrawable(R.drawable.level_rounded_layout_music));
                break;
            case Utility.VIP:
                Utility.setBackgroundToView(lockedlevelItemViewHolder.layout, context.getResources().getDrawable(R.drawable.level_rounded_layout_character));
                break;
        }

        return paramView;
    }

    static class UnlockedLevelItemViewHolder {
        TextView titleTextView;
        TextView percentageTextView;
        TextView solvedQuizTextView;
        TextView starsAchievedTextView;
        ProgressBar progressBar;
        Button button;
        RelativeLayout layout;
    }

    static class LockedLevelItemViewHolder {
        TextView titleTextView;
        TextView toUnlockTextView;
        ImageView lockImageView;
        RelativeLayout layout;
    }
}
