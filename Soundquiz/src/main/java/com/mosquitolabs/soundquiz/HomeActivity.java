package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {

    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPackageListActivity();
            }
        });

        test();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void startPackageListActivity() {
        Intent mIntent = new Intent(this, PackageListActivity.class);
        this.startActivity(mIntent);
    }

    private void test() {
        PackageCollection.getInstance().getPackageCollection().clear();
        PackageData cinema = new PackageData();
        String category = "Cinema";
        cinema.setCategory(category);

        LevelData level1 = new LevelData();
        level1.setCategory(category);

        for (int i = 0; i < 20; i++) {
            QuizData quizData = new QuizData();
            quizData.setCategory(level1.getCategory());
            switch (i) {
                case 0:
                    quizData.setAnswer("Fox");
                    break;
                case 1:
                    quizData.setAnswer("Columbia");
                    break;
                case 2:
                    quizData.setAnswer("Lucas Film");
                    break;
                case 3:
                    quizData.setAnswer("Warner Bros");
                    break;
                case 4:
                    quizData.setAnswer("Universal");
                    break;
                case 5:
                    quizData.setAnswer("MGM");
                    break;
                case 6:
                    quizData.setAnswer("Tristar");
                    break;
                case 7:
                    quizData.setAnswer("Fandango");
                    break;
                case 8:
                    quizData.setAnswer("New Line Cinema");
                    break;
                case 9:
                    quizData.setAnswer("Castle Rock");
                    break;
                case 10:
                    quizData.setAnswer("Lionsgate");
                    break;
                case 11:
                    quizData.setAnswer("Walt Disney");
                    break;

                default:
                    quizData.setAnswer("Fox");
                    break;
            }

            level1.getQuizList().add(quizData);
        }

        cinema.getLevelList().add(level1);
        cinema.getLevelList().add(level1);
        cinema.getLevelList().add(level1);
        cinema.getLevelList().add(level1);
        PackageCollection.getInstance().getPackageCollection().add(cinema);
        PackageCollection.getInstance().getPackageCollection().add(cinema);
        PackageCollection.getInstance().getPackageCollection().add(cinema);
        PackageCollection.getInstance().getPackageCollection().add(cinema);
        PackageCollection.getInstance().getPackageCollection().add(cinema);
        PackageCollection.getInstance().getPackageCollection().add(cinema);
    }


    public static int convertDpToPixels(float dp, Context context) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }
}