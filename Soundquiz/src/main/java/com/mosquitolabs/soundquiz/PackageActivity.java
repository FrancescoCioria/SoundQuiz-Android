package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class PackageActivity extends Activity {


    private int packageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        packageIndex = getIntent().getExtras().getInt("packageIndex");

        ListView levelListView = (ListView) findViewById(R.id.levelListView);
        LevelListViewAdapter levelListViewAdapter = new LevelListViewAdapter(this, packageIndex);
        levelListView.setAdapter(levelListViewAdapter);

        getActionBar().setTitle(PackageCollection.getInstance().getPackageCollection().get(packageIndex).getCategory() + Integer.toString(packageIndex + 1));
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.level_list, menu);
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

}
