package com.mosquitolabs.soundquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        if (paramView == null) {
            paramView = inflater.inflate(R.layout.quiz_list_item3,
                    null);
            quizItemViewHolder = new QuizItemViewHolder();

            quizItemViewHolder.divider = (LinearLayout) paramView.findViewById(R.id.divider);

            for (int i = 0; i < COLUMNS; i++) {
                final int index = paramInt * COLUMNS + i;
                final QuizData quizData = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(index);
                String imageId = "imageView" + (i + 1);
                String questionMarkId = "questionMark" + (i + 1);
                String bodyId = "body" + (i + 1);
                int resImage = context.getResources().getIdentifier(imageId, "id", context.getPackageName());
                int resQuestionMark = context.getResources().getIdentifier(questionMarkId, "id", context.getPackageName());
                int resBody = context.getResources().getIdentifier(bodyId, "id", context.getPackageName());
                quizItemViewHolder.images[i] = (ImageView) paramView.findViewById(resImage);
                quizItemViewHolder.bodies[i] = (TextView) paramView.findViewById(resBody);
//                quizItemViewHolder.questionMarks[i] = (ImageView) paramView.findViewById(resQuestionMark);

                quizItemViewHolder.images[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startQuizActivity(quizData, index);
                    }
                });

            }

            paramView.setTag(quizItemViewHolder);
        } else {
            quizItemViewHolder = (QuizItemViewHolder) paramView.getTag();
        }

        boolean divider = true;

        for (int i = 0; i < COLUMNS; i++) {
            final int index = paramInt * COLUMNS + i;
            final QuizData quizData = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(index);
            String path;
            switch (index) {
                case 0:
                    path = "fox";
                    break;
                case 1:
                    path = "columbia";
                    break;
                case 2:
                    path = "paramount";
                    break;
                case 3:
                    path = "warner_bros";
                    break;
                case 4:
                    path = "universal";
                    break;
                case 5:
                    path = "mgm";
                    break;
                case 6:
                    path = "walt_disney";
                    break;
                case 7:
                    path = "fandango";
                    break;
                case 8:
                    path = "new_line_cinema";
                    break;
                case 9:
                    path = "castle_rock";
                    break;
                case 10:
                    path = "lionsgate";
                    break;
                case 11:
                    path = "image";
                    break;
                default:
                    path = "image";
                    break;
            }


            if (quizData.isSolved()) {
                int res = context.getResources().getIdentifier(path, "drawable", context.getPackageName());
                quizItemViewHolder.images[i].setImageDrawable(context.getResources().getDrawable(res));
                quizItemViewHolder.bodies[i].setText(quizData.getAnswer());
                quizItemViewHolder.bodies[i].setVisibility(View.VISIBLE);
//                quizItemViewHolder.questionMarks[i].setVisibility(View.GONE);
            } else {
                int res = context.getResources().getIdentifier(path + "_blur", "drawable", context.getPackageName());
                quizItemViewHolder.images[i].setImageDrawable(context.getResources().getDrawable(res));
                quizItemViewHolder.bodies[i].setVisibility(View.GONE);
//                quizItemViewHolder.questionMarks[i].setVisibility(View.VISIBLE);
            }
        }


        if ((paramInt * COLUMNS + (COLUMNS - 1)) == packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().size() - 1) {
            quizItemViewHolder.divider.setVisibility(View.VISIBLE);
        } else {
            quizItemViewHolder.divider.setVisibility(View.GONE);
        }

        return paramView;
    }

    static class QuizItemViewHolder {
        TextView bodies[] = {null, null, null, null};
        ImageView images[] = {null, null, null, null};
        //        ImageView questionMarks[] = {null, null, null, null};
        LinearLayout divider;
    }


}