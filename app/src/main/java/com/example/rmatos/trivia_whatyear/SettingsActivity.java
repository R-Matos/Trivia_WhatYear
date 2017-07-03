package com.example.rmatos.trivia_whatyear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by rmatos on 02/07/17.
 */

public class SettingsActivity extends Activity {

    private Spinner spinner_difficulty;
    private Switch switch_music;
    private Switch switch_sound;
    private Switch switch_vibration;
    private EditText et_name;
    private EditText et_email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);   //Removes any focus
        setContentView(R.layout.activity_settings);

        initialiseViews();
        setState();                                                                                     //Sets state depending on data passed from previous activity
    }

    private void initialiseViews() {
        spinner_difficulty = (Spinner) findViewById(R.id.spinner_difficulty);
        switch_music = (Switch) findViewById(R.id.switch_music);
        switch_sound = (Switch) findViewById(R.id.switch_sound);
        switch_vibration = (Switch) findViewById(R.id.switch_vibration);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
    }

    private void setState() {
        Intent activityThatCalled = getIntent();

        String difficulty = activityThatCalled.getExtras().getString("difficulty");

        if (difficulty.equals("EASY")) {
            spinner_difficulty.setSelection(0);
        } else if (difficulty.equals("NORMAL")) {
            spinner_difficulty.setSelection(1);
        } else if (difficulty.equals("HARD")) {
            spinner_difficulty.setSelection(2);
        }

        switch_music.setChecked(activityThatCalled.getExtras().getBoolean("isMusicOn"));
        switch_sound.setChecked(activityThatCalled.getExtras().getBoolean("isSoundsOn"));
        switch_vibration.setChecked(activityThatCalled.getExtras().getBoolean("isVibrationOn"));
        et_name.setText(activityThatCalled.getExtras().getString("name"));
        et_email.setText(activityThatCalled.getExtras().getString("email"));
    }

    //Returns current state to previous activity
    public void onSave(View view) {
        Intent goingBack = new Intent();

        goingBack.putExtra("difficulty", spinner_difficulty.getSelectedItem().toString());
        goingBack.putExtra("music", switch_music.isChecked());
        goingBack.putExtra("sound", switch_sound.isChecked());
        goingBack.putExtra("vibration", switch_vibration.isChecked());
        goingBack.putExtra("name", et_name.getText().toString());
        goingBack.putExtra("email", et_email.getText().toString());

        setResult(RESULT_OK, goingBack);
        finish();
    }




}
