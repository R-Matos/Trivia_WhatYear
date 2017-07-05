package com.example.rmatos.trivia_whatyear;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    private String difficulty;
    private Categories categories;
    private DataBaseHelper myDbHelper;
    private ArrayList<String> questions = new ArrayList<>();
    private int currentQuestionPos = 0;



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
        twQuestionStatus.setText("Q" + currentQuestionPos+1 + " of " + numberOfQuestions);

        //Updates question text
        twQuestion.setText(questions.get(currentQuestionPos));

        //


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
        int randomNumber = rand.nextInt(possibleQuestions.size());

        //Adds a question from possibleQuestions to question array.
        for (int i = 0; i < numberOfQuestions; i++) {
            questions.add(possibleQuestions.get(randomNumber));
            possibleQuestions.remove(randomNumber);                                                 //Ensures same question is not used
        }

        return questions;
    }

    public void onPrevious(View view) {
    }

    public void onSkip(View view) {
    }



}
