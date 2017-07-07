package com.example.rmatos.trivia_whatyear;

import android.util.Log;

import java.util.Random;

/**
 * Created by RMatos on 06/07/2017.
 */

public class Question {

    private String question;
    private int year;
    private int month;
    private int day;
    private String excerpt;
    private String category;
    private String imageURL;
    private String imageCaption;

    private int possibleAnswer1;
    private int possibleAnswer2;
    private int possibleAnswer3;
    private int possibleAnswer4;
    private int buttonSubmitted;

    public boolean isAnsweredCorrectly() {
        return answeredCorrectly;
    }

    public void setAnsweredCorrectly(boolean answeredCorrectly) {
        this.answeredCorrectly = answeredCorrectly;
    }

    private boolean answeredCorrectly;


    public Question(String question, int year, int month, int day, String excerpt, String category, String imageURL, String imageCaption) {

        this.question = question;
        this.year = year;
        this.month = month;
        this.day = day;
        this.excerpt = excerpt;
        this.category = category;
        this.imageURL = imageURL;
        this.imageCaption = imageCaption;
    }

    public String getQuestion() {
        return question;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getCategory() {
        return category;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public int getPossibleAnswer1() {
        return possibleAnswer1;
    }

    public int getPossibleAnswer2() {
        return possibleAnswer2;
    }

    public int getPossibleAnswer3() {
        return possibleAnswer3;
    }

    public int getPossibleAnswer4() {
        return possibleAnswer4;
    }

    public void setButtonSubmitted(int buttonNumber) {
        this.buttonSubmitted = buttonNumber;
    }

    public int getButtonSubmitted() {
        return buttonSubmitted;
    }





    public void setPossibleAnswers(String difficulty) {

        Random rand = new Random();

       //Range depends on difficulty chosen
        int range = 0;

        //Below random whether range is added or subtracted from answer
        possibleAnswer1 = ((rand.nextBoolean()) ? year+getRange(difficulty) : year-getRange(difficulty));
        possibleAnswer2 = ((rand.nextBoolean()) ? year+getRange(difficulty) : year-getRange(difficulty));
        possibleAnswer3 = ((rand.nextBoolean()) ? year+getRange(difficulty) : year-getRange(difficulty));
        possibleAnswer4 = ((rand.nextBoolean()) ? year+getRange(difficulty) : year-getRange(difficulty));


        //Below ensures no identical answers
        boolean isAnswersUnique = true;
        do {

            //Randomly alters answers by +/- 1
            possibleAnswer1 = ((rand.nextBoolean()) ? possibleAnswer1+1 : possibleAnswer1-1);
            possibleAnswer2 = ((rand.nextBoolean()) ? possibleAnswer2+1 : possibleAnswer2-1);
            possibleAnswer3 = ((rand.nextBoolean()) ? possibleAnswer3+1 : possibleAnswer3-1);
            possibleAnswer4 = ((rand.nextBoolean()) ? possibleAnswer4+1 : possibleAnswer4-1);

            //Check for identical answers
            if (possibleAnswer1 == possibleAnswer2 || possibleAnswer1 == possibleAnswer3
                    || possibleAnswer1 == possibleAnswer4 || possibleAnswer2 == possibleAnswer3
                    || possibleAnswer2 == possibleAnswer4 || possibleAnswer3 == possibleAnswer4)
                isAnswersUnique = false;
            else
                isAnswersUnique = true;

        } while (!isAnswersUnique);


        //Check if a button already contains the correct answer
        if (possibleAnswer1 == year || possibleAnswer2 == year || possibleAnswer3 == year || possibleAnswer4 == year)
            return;

        //Decide which button contains the correct answer
        int button = rand.nextInt(4)+1;
        if (button == 1) {
            possibleAnswer1 = year;
        } else if (button == 2){
            possibleAnswer2 = year;
        } else if (button == 3){
            possibleAnswer3 = year;
        } else if (button == 4){
            possibleAnswer4 = year;
        } else {
            Log.wtf("Question,setButtons", "button not in range");
        }

    }


    private int getRange(String difficulty) {

        Random rand = new Random();
        int range = 0;

        if (difficulty.equals("Easy")) {
            range = rand.nextInt(20);
        } else if (difficulty.equals("Normal")) {
            range = rand.nextInt(10);
        } else if (difficulty.equals("Hard")) {
            range = rand.nextInt(5);
        } else {
            Log.wtf("Question,setButtons", "Invalid difficulty");
        }

        return range;
    }





}
