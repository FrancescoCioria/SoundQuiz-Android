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

        LevelItemViewHolder levelItemViewHolder;

        if (paramView == null) {

            paramView = inflater.inflate(R.layout.level_list_item,
                    null);
            levelItemViewHolder = new LevelItemViewHolder();

            levelItemViewHolder.button = (Button) paramView.findViewById(R.id.playButton);
            levelItemViewHolder.layout = (RelativeLayout) paramView.findViewById(R.id.layout);

            levelItemViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLevelActivity(paramInt);
                }
            });


            levelItemViewHolder.layout.getLayoutParams().width = Utility.getWidth(context) * 9 / 10;

            paramView.setTag(levelItemViewHolder);
        } else {
            levelItemViewHolder = (LevelItemViewHolder) paramView.getTag();
        }


        return paramView;
    }

    static class LevelItemViewHolder {
        TextView body;
        ImageView image;
        Button button;
        RelativeLayout layout;
    }
}
