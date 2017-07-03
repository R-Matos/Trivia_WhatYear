package com.example.rmatos.trivia_whatyear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static String difficulty = "NORMAL";
    private static boolean isMusicOn = true;
    private static boolean isSoundsOn = true;
    private static boolean isVibrationOn = true;
    private static String name = "John Doe";
    private static String email = "email@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Get previous sesion data to populate variables. Then delete varialbes default values.

    }


    //Inflates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Menu events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Opens settings activity
        if (id == R.id.menu_settings)
        {
            Intent getSettingsScreenIntent = new Intent(this, SettingsActivity.class);
            final int result = 1;

            getSettingsScreenIntent.putExtra("difficulty", difficulty);
            getSettingsScreenIntent.putExtra("isMusicOn", isMusicOn);
            getSettingsScreenIntent.putExtra("isSoundsOn", isSoundsOn);
            getSettingsScreenIntent.putExtra("isVibrationOn", isVibrationOn);
            getSettingsScreenIntent.putExtra("name", name);
            getSettingsScreenIntent.putExtra("email", email);

            startActivityForResult(getSettingsScreenIntent, result);
        }


        return super.onOptionsItemSelected(item);
    }

    //Obtains data from previous activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        difficulty = data.getStringExtra("difficulty");
        isMusicOn = data.getBooleanExtra("music", false);
        isSoundsOn = data.getBooleanExtra("sound", false);
        isVibrationOn = data.getBooleanExtra("vibration", false);
        name = data.getStringExtra("name");
        email = data.getStringExtra("email");


    }

}
