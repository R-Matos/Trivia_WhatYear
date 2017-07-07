package com.example.rmatos.trivia_whatyear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by RMatos on 05/07/2017.
 */

public class PlayActivity extends Activity {

    private Context context = this;

    //View components
    private TextView twQuestionStatus;
    private TextView twCategory;
    private TextView twQuestion;
    private ProgressBar progressBar;
    private TextView twProgressTime;
    private ToggleButton btnAnswer1;
    private ToggleButton btnAnswer2;
    private ToggleButton btnAnswer3;
    private ToggleButton btnAnswer4;
    private View answerButtonView;
    private TextView twSubmit;

    //Misc Variables
    private final int numberOfQuestions = 20;
    private final int timeLimit = 20;
    private String difficulty;
    private Categories categories;
    private DataBaseHelper myDbHelper;
    private ArrayList<Question> questions = new ArrayList<>();
    private int currentQuestionPos = 0;

    //Flags & counters
    private boolean resetProgressBar;
    private int timeCounter;
    private boolean isCircleRed;
    private boolean isProgressBarThreadRunning;
    private boolean clickNextQuestion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Sets-up view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //Makes reference to views
        twQuestionStatus = (TextView) findViewById(R.id.tw_question_status);
        twCategory = (TextView) findViewById(R.id.tw_category);
        twQuestion = (TextView) findViewById(R.id.tw_question);
        progressBar = (ProgressBar) findViewById(R.id.play_progress_bar);
        twProgressTime = (TextView) findViewById(R.id.tw_time);
        btnAnswer1 = (ToggleButton) findViewById(R.id.btn_answer_1);
        btnAnswer2 = (ToggleButton) findViewById(R.id.btn_answer_2);
        btnAnswer3 = (ToggleButton) findViewById(R.id.btn_answer_3);
        btnAnswer4 = (ToggleButton) findViewById(R.id.btn_answer_4);
        answerButtonView = findViewById(R.id.answer_button_view);                                   //Needed to refresh buttons (invalidate)
        twSubmit = (TextView) findViewById(R.id.tw_play_submit);

        btnAnswer1.setOnCheckedChangeListener(answerListener);
        btnAnswer2.setOnCheckedChangeListener(answerListener);
        btnAnswer3.setOnCheckedChangeListener(answerListener);
        btnAnswer4.setOnCheckedChangeListener(answerListener);

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
        ArrayList<Question> possibleQuestions = getPossibleQuestions(categoriesSelected);
        questions = getQuestions(possibleQuestions);

        //Prerpares view for first question
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

        //Reset Variables
        currentQuestionPos++;

        //Reset timer
        resetProgressBar = true;
        while (isProgressBarThreadRunning) {}                                                       //Waits till previous thread has ended
        startProgressBar();

        //Updates question status (top-bar)
        twQuestionStatus.setText("Q " + currentQuestionPos + " of " + numberOfQuestions);

        //Updates category
        twCategory.setText(questions.get(currentQuestionPos).getCategory());

        //Updates question text
        twQuestion.setText(questions.get(currentQuestionPos).getQuestion());

        //Updates buttons
            //Removes any highlighting/backround change
        btnAnswer1.setBackgroundResource(R.drawable.answer_buttons);
        btnAnswer2.setBackgroundResource(R.drawable.answer_buttons);
        btnAnswer3.setBackgroundResource(R.drawable.answer_buttons);
        btnAnswer4.setBackgroundResource(R.drawable.answer_buttons);
            //Ensure buttons are unchecked
        btnAnswer1.setChecked(false);
        btnAnswer2.setChecked(false);
        btnAnswer3.setChecked(false);
        btnAnswer4.setChecked(false);
            //Sets buttons text
        btnAnswer1.setText(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer1()));
        btnAnswer2.setText(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer2()));
        btnAnswer3.setText(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer3()));
        btnAnswer4.setText(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer4()));
            //Refreshes buttons
        answerButtonView.invalidate();

//        String tempString="Copyright";
//        SpannableString spanString = new SpannableString(tempString);
//        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
//        btnAnswer1.setText(spanString);

        //Animations (slide in)
        animate(twCategory, R.anim.in_right_to_left);
        animate(twQuestion, R.anim.in_left_to_right);
        animate(twSubmit, R.anim.in_left_to_right);



    }


    //Gets the possible questions in each category the user selected
    private ArrayList<Question> getPossibleQuestions(ArrayList<String> categories) {

        //ArrayList<String> possibleQuestions = new ArrayList<>();
        ArrayList<Question> questions = new ArrayList<>();
        Cursor cursor;

        //Cycles through each category user selected
        for (String category : categories) {

            //Query
            cursor = myDbHelper.getReadableDatabase().rawQuery("SELECT * FROM Articles", null);

            int idColumn = cursor.getColumnIndex("_id");
            int yearColumn = cursor.getColumnIndex("Year");
            int monthColumn = cursor.getColumnIndex("Month");
            int dayColumn = cursor.getColumnIndex("Day");
            int excerptColumn = cursor.getColumnIndex("Excerpt");
            int categoryColumn = cursor.getColumnIndex("Category");
            int imageURLColumn = cursor.getColumnIndex("Image URL");
            int imageCaptionColumn = cursor.getColumnIndex("Image Caption");

            cursor.moveToFirst();

            if (cursor != null && (cursor.getCount() > 0)) {

                //Cycles through each row in table, creates a question obj and adds to questions array
                do {

                    String categoryCell = cursor.getString(categoryColumn);

                    if (categoryCell.equals(category)) {

                        String title = cursor.getString(idColumn);
                        int year = cursor.getInt(yearColumn);
                        int month = cursor.getInt(monthColumn);
                        int day = cursor.getInt(dayColumn);
                        String excerpt = cursor.getString(excerptColumn);
                        String imageURL = cursor.getString(imageURLColumn);
                        String imageCaption = cursor.getString(imageCaptionColumn);

                        Question question = new Question(title, year, month, day, excerpt, category,
                                imageURL, imageCaption);
                        questions.add(question);
                    }
                } while (cursor.moveToNext());

            }
        }

        return questions;
    }


    private ArrayList<Question> getQuestions(ArrayList<Question> possibleQuestions) {

        ArrayList<Question> questions = new ArrayList<>();

        //Random number generator
        Random rand = new Random();
        int randomNumber = rand.nextInt(possibleQuestions.size());

        //Adds a question from possibleQuestions to question array.
        for (int i = 0; i < numberOfQuestions; i++) {
            Question question = possibleQuestions.get(randomNumber);                                //Gets a questions from possibleQuestions
            question.setPossibleAnswers(difficulty);                                                //Prepares possible answers for buttons
            //System.out.println(question.getPossibleAnswer1());
            questions.add(question);                                                                //Adds questions to question array
            possibleQuestions.remove(randomNumber);                                                 //Ensures same question is not used
        }

        return questions;
    }


    private void checkSubmission() {

        //Checks which button/answer user selected
        if (btnAnswer1.isChecked()) {
            questions.get(currentQuestionPos).setButtonSubmitted(1);
            if (btnAnswer1.getText().equals(String.valueOf(questions.get(currentQuestionPos).getYear())))
                correct();
            else
                incorrect();
        } else if (btnAnswer2.isChecked()) {
            questions.get(currentQuestionPos).setButtonSubmitted(2);
            if (btnAnswer2.getText().equals(String.valueOf(questions.get(currentQuestionPos).getYear())))
                correct();
            else
                incorrect();

        } else if (btnAnswer3.isChecked()) {
            questions.get(currentQuestionPos).setButtonSubmitted(3);
            if (btnAnswer3.getText().equals(String.valueOf(questions.get(currentQuestionPos).getYear())))
                correct();
            else
                incorrect();
        } else if (btnAnswer4.isChecked()) {
            questions.get(currentQuestionPos).setButtonSubmitted(4);
            if (btnAnswer4.getText().equals(String.valueOf(questions.get(currentQuestionPos).getYear())))
                correct();
            else
                incorrect();

        } else {
            questions.get(currentQuestionPos).setButtonSubmitted(0);
            incorrect();
        }

    }


    private void correct() {
        int buttonSubmitted = questions.get(currentQuestionPos).getButtonSubmitted();
        questions.get(currentQuestionPos).setAnsweredCorrectly(true);

        //Highlights button clicked green
        if (buttonSubmitted == 1) {
            btnAnswer1.setBackgroundColor(Color.parseColor("#04DA31"));
        } else if (buttonSubmitted == 2) {
            btnAnswer2.setBackgroundColor(Color.parseColor("#04DA31"));
        } else if (buttonSubmitted == 3) {
            btnAnswer3.setBackgroundColor(Color.parseColor("#04DA31"));
        } else if (buttonSubmitted == 4) {
            btnAnswer4.setBackgroundColor(Color.parseColor("#04DA31"));
        }
    }


    private void incorrect() {
        int buttonSubmitted = questions.get(currentQuestionPos).getButtonSubmitted();
        questions.get(currentQuestionPos).setAnsweredCorrectly(false);
        String answer = String.valueOf(questions.get(currentQuestionPos).getYear());

        //Highlights button clicked red
        if (buttonSubmitted == 1) {
            btnAnswer1.setBackgroundColor(Color.parseColor("#FF2929"));
        } else if (buttonSubmitted == 2) {
            btnAnswer2.setBackgroundColor(Color.parseColor("#FF2929"));
        } else if (buttonSubmitted == 3) {
            btnAnswer3.setBackgroundColor(Color.parseColor("#FF2929"));
        } else if (buttonSubmitted == 4) {
            btnAnswer4.setBackgroundColor(Color.parseColor("#FF2929"));
        }

        //Determines correct answer and sets it green
        if (btnAnswer1.getText().equals(answer)) {
            btnAnswer1.setBackgroundColor(Color.parseColor("#04DA31"));
        } else if (btnAnswer2.getText().equals(answer)) {
            btnAnswer2.setBackgroundColor(Color.parseColor("#04DA31"));
        } else if (btnAnswer3.getText().equals(answer)) {
            btnAnswer3.setBackgroundColor(Color.parseColor("#04DA31"));
        } else if (btnAnswer4.getText().equals(answer)) {
            btnAnswer4.setBackgroundColor(Color.parseColor("#04DA31"));
        }
    }



    public void onPrevious(View view) {


    }

    public void onSubmit(View view) {

        //If clicked next to change question or clicked submit to check answer or
        if (clickNextQuestion) {

            //If completed all questions
            if (currentQuestionPos == numberOfQuestions) {
                endGameDialog();
            }

            clickNextQuestion = false;
            twSubmit.setText("SUBMIT");
//                //Animations slide out
            //TODO: Out animations without in immediately overriding
//            animate(twCategory, R.anim.out_right_to_left);
//            animate(twQuestion, R.anim.out_left_to_right);
//            animate(twSubmit, R.anim.out_left_to_right);
            nextQuestion();
            setButtonsClickable(true);
        } else {
            setButtonsClickable(false);
            checkSubmission();
            twSubmit.setText("NEXT");
            clickNextQuestion = true;

        }
    }

    private void endGameDialog() {

        String summary;
        int correctAnswers = 0;

        //Gets number of correctly answere questions
        for (Question question : questions) {
            if (question.isAnsweredCorrectly()) {
                correctAnswers++;
            }
        }

        summary = "Correctly answered "+correctAnswers+"/"+numberOfQuestions;





    }






    //Progress bar thread
    public void startProgressBar() {
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
        isProgressBarThreadRunning = true;

        final Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 1; i <= iterations; i++) {

                        Thread.sleep(iterations / (iterations/10));

                        //Ends thread if required to reset
                        if (!resetProgressBar && isProgressBarThreadRunning) {
                            //Increments progress counter
                            progressBarHandler.sendEmptyMessage(0);

                            //Decrements text time counter
                            if (i % 100 == 0)
                                progressBarHandler.sendEmptyMessage(1);

                            //Progress bar turns red
                            if (i >= 1500 && !isCircleRed)
                                progressBarHandler.sendEmptyMessage(2);
                        } else {
                            i = iterations;
                        }
                    }

                    resetProgressBar = false;
                    isProgressBarThreadRunning = false;
                    setButtonsClickable(false);
                    twSubmit.setText("ANSWER");
                }
                catch(Throwable t) {
                    Log.i("Play, PBar Thread","Thread Crash");
                }
                Log.i("Play, PBar Thread","Thread End");



            }
        });

        background.start();

        if (!isProgressBarThreadRunning) {

        }
    }

    //Handler for progress bar thread
    Handler progressBarHandler = new Handler(){
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


    //Listener to main 4 buttons containing possible answers
    CompoundButton.OnCheckedChangeListener answerListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            //Sets text for each button state
            btnAnswer1.setTextOn(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer1()));
            btnAnswer1.setTextOff(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer1()));
            btnAnswer2.setTextOn(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer2()));
            btnAnswer2.setTextOff(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer2()));
            btnAnswer3.setTextOn(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer3()));
            btnAnswer3.setTextOff(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer3()));
            btnAnswer4.setTextOn(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer4()));
            btnAnswer4.setTextOff(String.valueOf(questions.get(currentQuestionPos).getPossibleAnswer4()));

            if (isChecked){

                //Ensures only 1 button is checked
                if (buttonView == btnAnswer1) {
                    btnAnswer2.setChecked(false);
                    btnAnswer3.setChecked(false);
                    btnAnswer4.setChecked(false);
                } else if (buttonView == btnAnswer2) {
                    btnAnswer1.setChecked(false);
                    btnAnswer3.setChecked(false);
                    btnAnswer4.setChecked(false);
                } else if (buttonView == btnAnswer3) {
                    btnAnswer1.setChecked(false);
                    btnAnswer2.setChecked(false);
                    btnAnswer4.setChecked(false);
                } else if (buttonView == btnAnswer4) {
                    btnAnswer1.setChecked(false);
                    btnAnswer2.setChecked(false);
                    btnAnswer3.setChecked(false);
                }
            }

            //Refreshes view
            answerButtonView.invalidate();
        }
    };


    private void setButtonsClickable(boolean value) {
        btnAnswer1.setClickable(value);
        btnAnswer2.setClickable(value);
        btnAnswer3.setClickable(value);
        btnAnswer4.setClickable(value);
    }

    private void animate(View view, int resourceID) {

        Animation anim = AnimationUtils.loadAnimation(context, resourceID);
        anim.reset();
        view.clearAnimation();
        view.startAnimation(anim);

        long currentTime = System.currentTimeMillis();

    }









}
