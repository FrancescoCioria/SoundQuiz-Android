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
import android.widget.TextView;

/**
 * Created by francesco on 3/10/14.
 */
public class QuizGridViewAdapter extends BaseAdapter {

    private Activity context;
    private LayoutInflater inflater;
    private  QuizCollection quizCollection = QuizCollection.getInstance();

    public QuizGridViewAdapter(Activity paramContext) {
        this.inflater = LayoutInflater.from(paramContext);
        context = paramContext;
    }


    public int getCount() {
        return quizCollection.getQuizCollection().get(0).getQuizList().size();
    }

    public Object getItem(int paramInt) {
        return Integer.valueOf(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    private void startQuizActivity(QuizData quizData,int paramInt){
        Intent mIntent = new Intent(context, QuizActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("answer", quizData.getAnswer());
        bundle.putString("category", quizData.getCategroy());
        bundle.putInt("index", paramInt);
        mIntent.putExtras(bundle);

        context.startActivity(mIntent);
    }

    public View getView(final int paramInt, View paramView,
                        ViewGroup paramViewGroup) {

        QuizItemViewHolder quizItemViewHolder;
        final QuizData quizData = QuizCollection.getInstance().getQuizCollection().get(0).getQuizList().get(paramInt);

        if (paramView == null) {

            paramView = inflater.inflate(R.layout.grid_item_quiz,
                    null);
            quizItemViewHolder = new QuizItemViewHolder();
            quizItemViewHolder.body = (TextView) paramView.findViewById(R.id.body);
            quizItemViewHolder.image = (ImageView) paramView.findViewById(R.id.image);
            quizItemViewHolder.button = (Button) paramView.findViewById(R.id.quizButton);

            quizItemViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuizActivity(quizData,paramInt);
                }
            });

            paramView.setTag(quizItemViewHolder);
        } else {
            quizItemViewHolder = (QuizItemViewHolder) paramView.getTag();
        }

        if(quizData.isSolved()) {
            quizItemViewHolder.body.setText(quizData.getAnswer());
        }else{
            quizItemViewHolder.body.setText(Integer.toString(paramInt + 1)+"?");
        }


        return paramView;
    }

    static class QuizItemViewHolder {
        TextView body;
        ImageView image;
        Button button;
    }
}
