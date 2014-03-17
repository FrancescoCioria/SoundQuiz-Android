package com.mosquitolabs.soundquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PackageFragment extends Fragment {


    public static PackageFragment newInstance(int packageIndex) {

        PackageFragment f = new PackageFragment();
        Bundle b = new Bundle();
        b.putInt("packageIndex", packageIndex);

        f.setArguments(b);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_package, container, false);

        final int packageIndex = getArguments().getInt("packageIndex", 0);

        TextView name = (TextView) rootView.findViewById(R.id.nameTextView);
        Button play = (Button) rootView.findViewById(R.id.playButton);
        RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.fragmentRelativeLayout);
        RelativeLayout header = (RelativeLayout) rootView.findViewById(R.id.header);
        RelativeLayout body = (RelativeLayout) rootView.findViewById(R.id.body);

        rootView.findViewById(R.id.parentRelativeLayout).setTag(packageIndex);

        final PackageListActivity activity = (PackageListActivity) getActivity();

        int width = activity.getPackageWidth();
        int height = activity.getPackageHeigth();

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

        name.setText(PackageCollection.getInstance().getPackageCollection().get(packageIndex).getCategory());
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
        getActivity().startActivity(mIntent);
    }
}
