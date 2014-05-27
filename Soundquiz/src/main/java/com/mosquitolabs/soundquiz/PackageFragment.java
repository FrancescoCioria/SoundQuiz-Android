package com.mosquitolabs.soundquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PackageFragment extends Fragment {


    public static PackageFragment newInstance(int packageIndex) {

        PackageFragment fragment = new PackageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("packageIndex", packageIndex);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_package_complete, container, false);
        final int packageIndex = getArguments().getInt("packageIndex", 0);


        TextView nameTextView = (TextView) rootView.findViewById(R.id.nameTextView);
        TextView unlockedTextView = (TextView) rootView.findViewById(R.id.unlockedTextView);
        TextView solvedTextView = (TextView) rootView.findViewById(R.id.solvedTextView);
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        Button play = (Button) rootView.findViewById(R.id.playButton);
        View layout = rootView.findViewById(R.id.fragmentRelativeLayout);
        RelativeLayout header = (RelativeLayout) rootView.findViewById(R.id.header);
        RelativeLayout body = (RelativeLayout) rootView.findViewById(R.id.body);

        rootView.findViewById(R.id.parentRelativeLayout).setTag(packageIndex);

//        switch (packageIndex) {
//            case Utility.CINEMA:
//                header.setBackgroundResource(R.drawable.fragment_round_header_cinema);
//                body.setBackgroundResource(R.drawable.fragment_round_body_cinema);
//                break;
//            case Utility.MUSIC:
//                header.setBackgroundResource(R.drawable.fragment_round_header_music);
//                body.setBackgroundResource(R.drawable.fragment_round_body_music);
//                break;
//            case Utility.VIP:
//                break;
//        }

        final PackageListActivity activity = (PackageListActivity) getActivity();

        int width = activity.getPackageWidth();
        int height = activity.getPackageHeight();

        int headerHeight = height / 5;
        int bodyHeight = height * 4 / 5;

        header.getLayoutParams().width = width;
        body.getLayoutParams().width = width;
        header.getLayoutParams().height = headerHeight;
        body.getLayoutParams().height = bodyHeight;

        layout.getLayoutParams().height = height;
        layout.getLayoutParams().width = width;

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getPager().getCurrentItem() != packageIndex) {
                    activity.getPager().setCurrentItem(packageIndex, true);
                }
            }
        });

        nameTextView.setText(PackageCollection.getInstance().getPackageCollection().get(packageIndex).getCategory());
        nameTextView.setTextSize(Utility.pixelsToSp(activity, headerHeight * 0.7f));

        unlockedTextView.setTextSize(Utility.pixelsToSp(activity, bodyHeight * 0.07f));
        solvedTextView.setTextSize(Utility.pixelsToSp(activity, bodyHeight * 0.07f));

//        progressBar.getLayoutParams().height = (int) (bodyHeight * 0.1f);

        int solvedQuizzes = 0;
        for (LevelData levelData : PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList()) {
            for (QuizData quizData : levelData.getQuizList()) {
                solvedQuizzes += quizData.isSolved() ? 1 : 0;
            }
        }
        int numberOfLevels = PackageCollection.getInstance().getPackageCollection().get(packageIndex).getLevelList().size();
        unlockedTextView.setText("unlocked: " + (solvedQuizzes / 10 + 1) + " / " + numberOfLevels);
        solvedTextView.setText("solved: " + solvedQuizzes + " / " + (numberOfLevels * 15));
        progressBar.setProgress(solvedQuizzes * 100 / (numberOfLevels * 15));

//        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gothic.ttf");
//        unlockedTextView.setTypeface(tf);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPackageActivity(packageIndex);
            }
        });

        return rootView;
    }

    private void startPackageActivity(int paramInt) {
        Intent mIntent = new Intent(getActivity(), PackageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("packageIndex", paramInt);
        mIntent.putExtras(bundle);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().startActivity(mIntent);
    }

}
