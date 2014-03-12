package com.mosquitolabs.soundquiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;


public class QuizListActivity extends Activity {

    private GridView quizGridView;
    private QuizGridViewAdapter quizGridViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizlist);
        //binding
        quizGridView = (GridView) findViewById(R.id.quizGridView);

        test();

        //set adapter
        quizGridViewAdapter = new QuizGridViewAdapter(this);
        quizGridView.setAdapter(quizGridViewAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshQuizGridAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz, menu);
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

    private void gameLogic() {


    }

    private void test() {
        QuizList quizList = new QuizList();
        quizList.setPath("lstCINEMA");
        for (int i = 0; i < 10; i++) {
            QuizData quizData = new QuizData();
            quizData.setCategory(quizList.getCategory());
            if (i == 0) {
                quizData.setAnswer("American Beauty");
            } else {
                quizData.setAnswer("ROCKY " + Integer.toString(i + 1));
            }
            quizList.getQuizList().add(quizData);
        }
        QuizCollection.getInstance().getQuizCollection().add(quizList);
    }


    private void refreshQuizGridAdapter() {
        if (quizGridViewAdapter != null) {
            QuizListActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    quizGridViewAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
