package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class PackageActivity extends Activity {

    private TextView back;
    private int packageIndex;
    private ListView levelListView;
    private LevelListViewAdapter levelListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utility.hideActionbar(this);

        packageIndex = getIntent().getExtras().getInt("packageIndex");
        levelListView = (ListView) findViewById(R.id.levelListView);
        back = (TextView) findViewById(R.id.back);

        levelListViewAdapter = new LevelListViewAdapter(this, packageIndex);
        levelListView.setAdapter(levelListViewAdapter);

        back.setText("< Categories");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
