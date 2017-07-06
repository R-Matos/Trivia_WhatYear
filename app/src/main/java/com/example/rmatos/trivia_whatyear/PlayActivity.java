package com.example.rmatos.trivia_whatyear;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by RMatos on 05/07/2017.
 */

public class PlayActivity extends Activity {

    private TextView twQuestionStatus;
    private TextView twQuestion;
    private ProgressBar progressBar;
    private TextView twProgressTime;
    private Button btnAnswer1;
    private Button btnAnswer2;
    private Button btnAnswer3;
    private Button btnAnswer4;

    private final int numberOfQuestions = 20;
    private final int timeLimit = 20;
    private String difficulty;
    private Categories categories;
    private DataBaseHelper myDbHelper;
    private ArrayList<String> questions = new ArrayList<>();
    private int currentQuestionPos = 0;

    private boolean resetProgressBar;
    private int timeCounter;
    private boolean isCircleRed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Sets-up view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //Makes reference to views
        twQuestionStatus = (TextView) findViewById(R.id.tw_question_status);
        twQuestion = (TextView) findViewById(R.id.tw_question);
        progressBar = (ProgressBar) findViewById(R.id.play_progress_bar);
        twProgressTime = (TextView) findViewById(R.id.tw_time);
        btnAnswer1 = (Button) findViewById(R.id.btn_answer_1);
        btnAnswer2 = (Button) findViewById(R.id.btn_answer_2);
        btnAnswer3 = (Button) findViewById(R.id.btn_answer_3);
        btnAnswer4 = (Button) findViewById(R.id.btn_answer_4);

        //Gets data passed from previous activity
        Intent activityThatCalled = getIntent();
        difficulty = activityThatCalled.getStringExtra("difficulty");
        categories = (Categories) activityThatCalled.getSerializableExtra("cbStates");

        //Sets-up database
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch (SQLException e) {
            throw new Error("Unable to open database");
        }

        //Creates questions
        ArrayList<String> categoriesSelected = categories.getSelected();
        ArrayList<String> possibleQuestions = getPossibleQuestions(categoriesSelected);
        questions = getQuestions(possibleQuestions);

        nextQuestion();

//        Cursor cursor = myDbHelper.getReadableDatabase().rawQuery("SELECT * FROM Articles", null);
//
//        int idColumn = cursor.getColumnIndex("_id");
//        //int nameColumn = cursor.getColumnIndex("");
//
//        cursor.moveToFirst();
//
//        if (cursor != null && (cursor.getCount() > 0)) {
//
//            do {
//                String id = cursor.getString(idColumn);
//                System.out.println(id);
//
//            } while (cursor.moveToNext());
//
//        }

    }


    //Updates UI and variable states for next question
    private void nextQuestion() {

        //Updates question status (top-bar)
        twQuestionStatus.setText("Q " + currentQuestionPos+1 + " of " + numberOfQuestions);

        //Updates question text
        twQuestion.setText(questions.get(currentQuestionPos));

        //Reset timer

//        progressBar.setVisibility(View.VISIBLE);

        progressBar.setProgress(180);
//        progressBar.setSecondaryProgress(10);


        //reset timer bar
        //do buttons



    }


    //Gets the possible questions in each category the user selected
    private ArrayList<String> getPossibleQuestions(ArrayList<String> categories) {

        ArrayList<String> possibleQuestions = new ArrayList<>();
        Cursor cursor;

        //Cycles through each category user selected
        for (String category : categories) {

            //Query
            cursor = myDbHelper.getReadableDatabase().rawQuery("SELECT * FROM Articles", null);

            int idColumn = cursor.getColumnIndex("_id");
            int categoryColumn = cursor.getColumnIndex("Category");


            cursor.moveToFirst();

            if (cursor != null && (cursor.getCount() > 0)) {

                //Cycles through each row in table
                do {

                    String cellCategory = cursor.getString(categoryColumn);

                    //If row contains category user selected save it's id ib possibleQuestions
                    if (cellCategory.equals(category)) {

                        String cellID = cursor.getString(idColumn);
                        possibleQuestions.add(cellID);
                    }
                } while (cursor.moveToNext());

            }
        }

        return possibleQuestions;
    }


    private ArrayList<String> getQuestions(ArrayList<String> possibleQuestions) {

        ArrayList<String> questions = new ArrayList<>();

        //Random number generator
        Random rand = new Random();
        int randomNumber = rand.nextInt(possibleQuestions.size()-2);

        //Adds a question from possibleQuestions to question array.
        for (int i = 0; i < numberOfQuestions; i++) {
            questions.add(possibleQuestions.get(randomNumber));
            possibleQuestions.remove(randomNumber);                                                 //Ensures same question is not used
        }

        return questions;
    }

    public void onPrevious(View view) {

        resetProgressBar = true;

        //Waits till previous thread has ended
        while (resetProgressBar) {}

        onStart();
    }

    public void onSkip(View view) {
        onStop();
    }






    //Progress bar thread
    public void onStart() {
        super.onStart();

        final int iterations = timeLimit * 100;
        progressBar.setMax(iterations);

        //Below Resets Variables
        timeCounter = timeLimit;
        twProgressTime.setText(String.valueOf(timeCounter));

        progressBar.setProgress(1);
        resetProgressBar = false;

        Rect bounds = progressBar.getProgressDrawable().getBounds();
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_blue));
        progressBar.getProgressDrawable().setBounds(bounds);
        isCircleRed = false;

        final Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    test: for (int i = 1; i <= iterations; i++) {

                        Thread.sleep(iterations / (iterations/10));

                        //Ends thread if required to reset
                        if (!resetProgressBar) {
                            //Increments progress counter
                            handler.sendEmptyMessage(0);

                            //Decrements text time counter
                            if (i % 100 == 0)
                                handler.sendEmptyMessage(1);

                            //Progress bar turns red
                            if (i >= 1500 && !isCircleRed)
                                handler.sendEmptyMessage(2);
                        } else {
                            i = iterations;
                        }
                    }
                }
                catch(Throwable t) {
                    // just end the background thread
                }
                Log.i("Play, PBar Thread","Thread End");
                resetProgressBar = false;
            }
        });

        background.start();
    }

    //Handler for progress bar thread
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0)
            {
                progressBar.incrementProgressBy(1);
            }
            else if (msg.what == 1)
            {
                timeCounter--;
                twProgressTime.setText(String.valueOf(timeCounter));
            }
            else if (msg.what == 2)
            {
                Rect bounds = progressBar.getProgressDrawable().getBounds();
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_red));
                progressBar.getProgressDrawable().setBounds(bounds);
                isCircleRed = true;
            }
        }
    };









}
