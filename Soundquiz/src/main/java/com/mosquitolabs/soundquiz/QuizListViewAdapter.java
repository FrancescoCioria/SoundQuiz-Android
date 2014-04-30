package com.mosquitolabs.soundquiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
            paramView = inflater.inflate(R.layout.quiz_list_item3,
                    null);
            quizItemViewHolder = new QuizItemViewHolder();

            quizItemViewHolder.divider = (LinearLayout) paramView.findViewById(R.id.divider);

            for (int i = 0; i < COLUMNS; i++) {
                final int index = paramInt * COLUMNS + i;
                final QuizData quizData = packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().get(index);
                String imageId = "imageView" + (i + 1);
                String bodyId = "body" + (i + 1);
                int resImage = context.getResources().getIdentifier(imageId, "id", context.getPackageName());
                int resBody = context.getResources().getIdentifier(bodyId, "id", context.getPackageName());
                quizItemViewHolder.images[i] = (CircularImageView) paramView.findViewById(resImage);
                quizItemViewHolder.bodies[i] = (TextView) paramView.findViewById(resBody);

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
            int imageWidth = (Utility.getWidth(context) - Utility.convertDpToPixels(context, 30)) / 3;
            int imageHeight = packageIndex == Utility.CINEMA ? imageWidth * 3 / 2 : imageWidth;


            quizItemViewHolder.images[i].getLayoutParams().height = imageHeight;
            quizItemViewHolder.images[i].setBorderWidth(1);

            if (packageIndex == Utility.VIP) {
                quizItemViewHolder.images[i].setCircularShape();
            } else {
                quizItemViewHolder.images[i].setRectangularShape();
            }

            int res;
            if (quizData.isSolved()) {
                res = context.getResources().getIdentifier(path, "drawable", context.getPackageName());
                try {
                    quizItemViewHolder.images[i].setImageDrawable(context.getResources().getDrawable(res));
                } catch (Exception e) {
                    quizItemViewHolder.images[i].setImageDrawable(context.getResources().getDrawable(R.drawable.twenty_century_fox));
                }
//                quizItemViewHolder.images[i].setImageBitmap(Utility.readImageFromDisk(context,quizData.getID(),false));
                quizItemViewHolder.bodies[i].setText(quizData.getAnswer());
                quizItemViewHolder.bodies[i].setVisibility(View.VISIBLE);
            } else {
                res = context.getResources().getIdentifier(path + "_blur", "drawable", context.getPackageName());
                try {
                    quizItemViewHolder.images[i].setImageDrawable(context.getResources().getDrawable(res));
                } catch (Exception e) {
                    quizItemViewHolder.images[i].setImageDrawable(context.getResources().getDrawable(R.drawable.twenty_century_fox_blur));
                }
//                quizItemViewHolder.images[i].setImageBitmap(Utility.readImageFromDisk(context,quizData.getID(),true));
                quizItemViewHolder.bodies[i].setVisibility(View.GONE);
            }

            quizItemViewHolder.bodies[i].setVisibility(View.GONE);
            quizItemViewHolder.images[i].setBorderColor(Color.parseColor("#b5b5b5"));
            quizItemViewHolder.images[i].setBorderColor(Color.parseColor("#80ffffff"));

        }


        if ((paramInt * COLUMNS + (COLUMNS - 1)) == packageCollection.getPackageCollection().get(packageIndex).getLevelList().get(levelIndex).getQuizList().size() - 1) {
            quizItemViewHolder.divider.setVisibility(View.VISIBLE);
        } else {
            quizItemViewHolder.divider.setVisibility(View.GONE);
        }

        return paramView;
    }

    static class QuizItemViewHolder {
        TextView bodies[] = {null, null, null};
        CircularImageView images[] = {null, null, null};
        LinearLayout divider;
    }


}