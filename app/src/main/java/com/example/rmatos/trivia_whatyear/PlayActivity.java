package com.example.rmatos.trivia_whatyear;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by RMatos on 05/07/2017.
 */

public class PlayActivity extends Activity {

    private Context context = this;

    private Dialog dialog;

    //View components
    private TextView tvQuestionStatus;
    private TextView tvCategory;
    private TextView tvQuestion;
    private ProgressBar progressBar;
    private TextView tvProgressTime;
    private ToggleButton btnAnswer1;
    private ToggleButton btnAnswer2;
    private ToggleButton btnAnswer3;
    private ToggleButton btnAnswer4;
    private View answerButtonView;
    private TextView tvSubmit;
    private ImageView askAudience;
    private ImageView fiftyfifty;
    private ImageView browse;
    private ImageView freezeTime;

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
    private boolean isAskAudienceUsed = false;

    //Lifeline variables
    private int answer1percentage = 0;
    private int answer2percentage = 0;
    private int answer3percentage = 0;
    private int answer4percentage = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Sets-up view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //Makes reference to views
        tvQuestionStatus = (TextView) findViewById(R.id.tw_question_status);
        tvCategory = (TextView) findViewById(R.id.tw_category);
        tvQuestion = (TextView) findViewById(R.id.tw_question);
        progressBar = (ProgressBar) findViewById(R.id.play_progress_bar);
        tvProgressTime = (TextView) findViewById(R.id.tw_time);
        btnAnswer1 = (ToggleButton) findViewById(R.id.btn_answer_1);
        btnAnswer2 = (ToggleButton) findViewById(R.id.btn_answer_2);
        btnAnswer3 = (ToggleButton) findViewById(R.id.btn_answer_3);
        btnAnswer4 = (ToggleButton) findViewById(R.id.btn_answer_4);
        answerButtonView = findViewById(R.id.answer_button_view);                                   //Needed to refresh buttons (invalidate)
        askAudience = (ImageView) findViewById(R.id.play_lifeline_askAudience);
        fiftyfifty = (ImageView) findViewById(R.id.play_lifeline_5050);
        browse = (ImageView) findViewById(R.id.play_lifeline_browse);
        freezeTime = (ImageView) findViewById(R.id.play_lifeline_freezeTime);




        tvSubmit = (TextView) findViewById(R.id.tw_play_submit);

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
        tvQuestionStatus.setText("Q " + currentQuestionPos + " of " + numberOfQuestions);

        //Updates category
        tvCategory.setText(questions.get(currentQuestionPos).getCategory());

        //Updates question text
        tvQuestion.setText(questions.get(currentQuestionPos).getQuestion());

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
//        btnAnswer1.setText(spanString)

        //Animations (slide in)
        animate(tvCategory, R.anim.in_right_to_left);
        animate(tvQuestion, R.anim.in_left_to_right);
        animate(tvSubmit, R.anim.in_left_to_right);



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


    public void onAskAudience(View view) {

        Question question = questions.get(currentQuestionPos);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);                                       //Removes action bar
        dialog.setContentView(R.layout.play_ask_audience);


        if (isAskAudienceUsed == false) {

            isAskAudienceUsed = true;

            boolean isAudienceCorrect = true;
            int buttonWithCorrectAnswer = 0;

            Random rand = new Random();
            int range = 0;

            range = rand.nextInt(10);//0-9

            //Determine whether the highest percentage will be the answer                               //Note 25% chance to be correct if selected wrong on if statement below
            if (difficulty.equals("Easy")) {                                                            //90% chance to be correct
                isAudienceCorrect = (range >= 2) ? true : false;
            } else if (difficulty.equals("Normal")) {                                                   //80% chance to be correct
                isAudienceCorrect = (range >= 3) ? true : false;
            } else if (difficulty.equals("Hard")) {                                                     //70% chance to be correct
                isAudienceCorrect = (range >= 4) ? true : false;
            } else {
                Log.wtf("PlayActivity,onAskAudience", "Invalid difficulty");
            }


            //Determines which button will contain correct answer
            if (isAudienceCorrect == true) {
                if (question.getPossibleAnswer1() == question.getYear()) {
                    buttonWithCorrectAnswer = 1;
                } else if (question.getPossibleAnswer2() == question.getYear()){
                    buttonWithCorrectAnswer = 2;
                } else if (question.getPossibleAnswer3() == question.getYear()){
                    buttonWithCorrectAnswer = 3;
                } else if (question.getPossibleAnswer4() == question.getYear()){
                    buttonWithCorrectAnswer = 4;
                }
            } else {
                range = rand.nextInt(4);

                if (range == 0) {
                    buttonWithCorrectAnswer = 1;
                } else if (range == 1) {
                    buttonWithCorrectAnswer = 2;
                } else if (range == 2) {
                    buttonWithCorrectAnswer = 3;
                } else if (range == 3) {
                    buttonWithCorrectAnswer = 4;
                }
            }


            range = rand.nextInt(50);

            //Creates percetnaages
            if (buttonWithCorrectAnswer == 1) {
                answer1percentage = 100 - range;
                range = rand.nextInt(100 - answer1percentage);
                answer2percentage = range;
                range = rand.nextInt(100 - (answer1percentage + answer2percentage));
                answer3percentage = range;
                range = 100 - (answer1percentage + answer2percentage + answer3percentage);
                answer4percentage = range;
            } else if (buttonWithCorrectAnswer == 2) {
                answer2percentage = 100 - range;
                range = rand.nextInt(100 - answer2percentage);
                answer1percentage = range;
                range = rand.nextInt(100 - (answer2percentage + answer1percentage));
                answer3percentage = range;
                range = 100 - (answer2percentage + answer1percentage + answer3percentage);
                answer4percentage = range;
            } else if (buttonWithCorrectAnswer == 3) {
                answer3percentage = 100 - range;
                range = rand.nextInt(100 - answer3percentage);
                answer1percentage = range;
                range = rand.nextInt(100 - (answer3percentage + answer1percentage));
                answer2percentage = range;
                range = 100 - (answer3percentage + answer1percentage + answer2percentage);
                answer4percentage = range;
            } else if (buttonWithCorrectAnswer == 4) {
                answer4percentage = 100 - range;
                range = rand.nextInt(100 - answer4percentage);
                answer1percentage = range;
                range = rand.nextInt(100 - (answer4percentage + answer1percentage));
                answer2percentage = range;
                range = 100 - (answer4percentage + answer1percentage + answer2percentage);
                answer3percentage = range;
            }

        } //END OF IF STATEMENT



        //Initialise chart
        com.github.mikephil.charting.charts.BarChart chart = new com.github.mikephil.charting.charts.BarChart(dialog.getContext());
        dialog.setContentView(chart);

        //Chart raw data
        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(answer1percentage, 0));
        entries.add(new BarEntry(answer2percentage, 2));
        entries.add(new BarEntry(answer3percentage, 4));
        entries.add(new BarEntry(answer4percentage, 6));

        //Chart dataset
        BarDataSet dataset = new BarDataSet(entries, "% of audience answer choice");
        dataset.setColor(getResources().getColor(R.color.orange));

        //Chart x-axis
        ArrayList<String> xaxis = new ArrayList<String>();
        xaxis.add(String.valueOf(question.getPossibleAnswer1()));
        xaxis.add("");
        xaxis.add(String.valueOf(question.getPossibleAnswer2()));
        xaxis.add("");
        xaxis.add(String.valueOf(question.getPossibleAnswer3()));
        xaxis.add("");
        xaxis.add(String.valueOf(question.getPossibleAnswer4()));

        //Create chart
        BarData barData = new BarData(xaxis, dataset);
        chart.setData(barData);
        chart.setDescription("");
        chart.invalidate();                                                                         // refresh
        chart.setDrawValueAboveBar(false);
        chart.setBackgroundColor(getResources().getColor(R.color.dark_green));
        chart.setGridBackgroundColor(getResources().getColor(R.color.dark_green));
        chart.setBorderColor(getResources().getColor(R.color.orange));

        dialog.show();
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
            tvSubmit.setText("SUBMIT");
//                //Animations slide out
            //TODO: Out animations without in immediately overriding
//            animate(twCategory, R.anim.out_right_to_left);
//            animate(twQuestion, R.anim.out_left_to_right);
//            animate(twSubmit, R.anim.out_left_to_right);
            nextQuestion();

            //Check lifelines
            if (isAskAudienceUsed) {
                askAudience.setClickable(false);
                askAudience.setAlpha(0.25f);
            }

            setButtonsClickable(true);
        } else {
            setButtonsClickable(false);
            checkSubmission();
            tvSubmit.setText("NEXT");
            clickNextQuestion = true;
        }
    }

    private void endGameDialog() {

//        String summary;
//        int correctAnswers = 0;
//
//        //Gets number of correctly answere questions
//        for (Question question : questions) {
//            if (question.isAnsweredCorrectly()) {
//                correctAnswers++;
//            }
//        }
//
//        summary = "Correctly answered "+correctAnswers+"/"+numberOfQuestions;
    }


    public void onInfo(View view) {

        Question question = questions.get(currentQuestionPos);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);                                       //Removes action bar
        dialog.setContentView(R.layout.play_information);

        //Reference Dialog views
        TextView dialog_category = (TextView) dialog.findViewById(R.id.play_dialog_category);
        TextView dialog_title = (TextView) dialog.findViewById(R.id.play_dialog_title);
        TextView dialog_caption = (TextView) dialog.findViewById(R.id.play_dialog_caption);
        TextView dialog_excerpt = (TextView) dialog.findViewById(R.id.play_info_excerpt);
        ImageView dialog_image = (ImageView) dialog.findViewById(R.id.play_dialog_image);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);

        dialog_category.setText(question.getCategory());
        dialog_title.setText(question.getQuestion());
        dialog_caption.setText(question.getImageCaption());
        dialog_excerpt.setText(question.getExcerpt());


        //TODO: Replace with async method
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL urlConnection = new URL(question.getImageURL());
            HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            dialog_image.setImageBitmap(myBitmap);
        } catch (Exception e) {
            Log.e("Play, dialog, ImageView", "Couldn't set image");
            e.printStackTrace();
        }

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }













    //Progress bar thread
    public void startProgressBar() {
        super.onStart();

        final int iterations = timeLimit * 100;
        progressBar.setMax(iterations);

        //Below Resets Variables
        timeCounter = timeLimit;
        tvProgressTime.setText(String.valueOf(timeCounter));

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
                    tvSubmit.setText("ANSWER");
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
                tvProgressTime.setText(String.valueOf(timeCounter));
            }
            else if (msg.what == 2)
            {
                Rect bounds = progressBar.getProgressDrawable().getBounds();
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_bar_red));
                progressBar.getProgressDrawable().setBounds(bounds);
                isCircleRed = true;
            }

            //Closes dialog if 5 seconds left
            if (dialog != null && tvProgressTime.getText().equals("5")) {
                dialog.cancel();
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
