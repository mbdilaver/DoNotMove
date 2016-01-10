/*
 * Author: Mustafa Burak Dilaver
 *
 * This program uses accelerometer sensor to inform user whether the phone moves or not.
 * User can see active and passive time.
 * User able to change the sensitivity for accelerometer sensor outputs.
 * User able to reset the time values.
 * Rotation is supported.
 */

package com.mbd.donotmove;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // Layout
    private TextView mActiveTimeText;
    private TextView mPassiveTimeText;
    private Button mFirstButton;
    private Button mSecondButton;
    private Button mThirdButton;

    // Sensor
    private SensorManager mSensorManager;
    private Sensor mSensor;

    // Last measurements from the sensor will be store to compare with new values
    float xLast = 0;
    float yLast = 0;
    float zLast = 0;

    // Time variables
    long totalActiveTime = 0;
    long totalPassiveTime = 0;
    long time;

    // Epsilon values
    final static double EPSILON_1 = 0.1;
    final static double EPSILON_2 = 1.0;
    final static double EPSILON_3 = 5.0;
    // Epsilon variable to compare sensor values. Default value is equal to EPSILON_1's value
    double epsilon = EPSILON_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = System.currentTimeMillis();

        // Assign layout elements
        mActiveTimeText = (TextView) findViewById(R.id.activeTimeTextView);
        mPassiveTimeText = (TextView) findViewById(R.id.passiveTimeTextView);
        mFirstButton = (Button) findViewById(R.id.sensButton1);
        mSecondButton = (Button) findViewById(R.id.sensButton2);
        mThirdButton = (Button) findViewById(R.id.sensButton3);

        mFirstButton.setText(EPSILON_1 + "");
        mSecondButton.setText(EPSILON_2 + "");
        mThirdButton.setText(EPSILON_3 + "");

        // Create our sensor manager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // We will use accelerometer sensor
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Get sensor values
        float xValue = event.values[0];
        float yValue = event.values[1];
        float zValue = event.values[2];

        // if any of the sensor values changed more than 'epsilon', isActive is true
        boolean isActive = xValue - xLast > epsilon ||
                           xValue - xLast < epsilon * -1 ||
                           yValue - yLast > epsilon ||
                           yValue - yLast < epsilon * -1 ||
                           zValue - zLast > epsilon ||
                           zValue - zLast < epsilon * -1;

        // to print time values more readable
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

        if ( isActive )
        {
            // Change active time label's background so user can understand which time is increasing
            mActiveTimeText.setBackgroundResource(R.drawable.button_background);
            mPassiveTimeText.setBackgroundColor(Color.TRANSPARENT);
            // Print active time
            Date rd = new Date(totalActiveTime);
            mActiveTimeText.setText(sdf.format(rd));
            // Update total active time
            totalActiveTime += System.currentTimeMillis() - time;
        }
        else
        {
            // Change passive time label's background so user can understand which time is increasing
            mPassiveTimeText.setBackgroundResource(R.drawable.button_background);
            mActiveTimeText.setBackgroundColor(Color.TRANSPARENT);
            // Print passive time
            Date rd = new Date(totalPassiveTime);
            mPassiveTimeText.setText(sdf.format(rd));
            // Update total passive time
            totalPassiveTime += System.currentTimeMillis() - time;
        }
        time = System.currentTimeMillis();
        // Save current sensor values so a comparison can be made at next sensor reading
        xLast = xValue;
        yLast = yValue;
        zLast = zValue;
    }

    public void resetButtonClicked(View view) {
        // Reset all time values
        totalActiveTime = 0;
        totalPassiveTime = 0;
        // Reset time labels
        mActiveTimeText.setText("00:00");
        mPassiveTimeText.setText("00:00");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save parameters
        outState.putLong("TOTAL_ACTIVE_TIME", totalActiveTime);
        outState.putLong("TOTAL_PASSIVE_TIME", totalPassiveTime);
        outState.putDouble("EPSILON", epsilon);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Get values from Bundle
        totalActiveTime = savedInstanceState.getLong("TOTAL_ACTIVE_TIME");
        totalPassiveTime = savedInstanceState.getLong("TOTAL_PASSIVE_TIME");
        epsilon = savedInstanceState.getDouble("EPSILON");

        // Print previous time values
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        Date rd = new Date(totalActiveTime);
        mActiveTimeText.setText(sdf.format(rd));
        rd.setTime(totalPassiveTime);
        mPassiveTimeText.setText(sdf.format(rd));

        // Re-color selected button's background
        switch(epsilon + "") {
            case EPSILON_1 + "": mFirstButton.setBackgroundColor(Color.parseColor("#f2b632"));
                            break;
            case EPSILON_2 + "": mSecondButton.setBackgroundColor(Color.parseColor("#f2b632"));
                            break;
            case EPSILON_3 + "": mThirdButton.setBackgroundColor(Color.parseColor("#f2b632"));
                            break;
        }
    }

    public void onFirstSensButton(View view) {
        epsilon = EPSILON_1;
        // Change the background color of clicked button to something different
        // so user can see which button is clicked
        view.setBackgroundColor(Color.parseColor("#f2b632"));
        // Change the background color of the others to their default color
        mSecondButton.setBackgroundColor(Color.parseColor("#06000a"));
        mThirdButton.setBackgroundColor(Color.parseColor("#06000a"));
    }

    public void onSecondSensButton(View view) {
        epsilon = EPSILON_2;
        // Change the background color of clicked button to something different
        // so user can see which button is clicked
        view.setBackgroundColor(Color.parseColor("#f2b632"));
        // Change the background color of the others to their default color
        mFirstButton.setBackgroundColor(Color.parseColor("#06000a"));
        mThirdButton.setBackgroundColor(Color.parseColor("#06000a"));
    }

    public void onThirdSensButton(View view) {
        epsilon = EPSILON_3;
        // Change the background color of clicked button to something different
        // so user can see which button is clicked
        view.setBackgroundColor(Color.parseColor("#f2b632"));
        // Change the background color of the others to their default color
        mFirstButton.setBackgroundColor(Color.parseColor("#06000a"));
        mSecondButton.setBackgroundColor(Color.parseColor("#06000a"));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // We aren't using it
    }
}
