package edu.cnm.deepdive.strataphor.controller;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import edu.cnm.deepdive.android.BaseFluentAsyncTask;
import edu.cnm.deepdive.strataphor.R;
import edu.cnm.deepdive.strataphor.model.StratAphorDatabase;
import edu.cnm.deepdive.strataphor.service.RandomSaying;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener,
    OnClickListener {

  private static final int FADE_DURATION = 8000;

  private TextView pithySaying;
  private float accel;
  private float accelCurrent;
  private float accelLast;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        SensorManager.SENSOR_DELAY_NORMAL);
    accel = 0.00f;
    accelCurrent = SensorManager.GRAVITY_EARTH;
    accelLast = SensorManager.GRAVITY_EARTH;
    pithySaying = findViewById(R.id.pithy_saying);
    View answerBackground = findViewById(R.id.answer_background);
    answerBackground.setOnClickListener(this);
  }

  @Override
  public void onSensorChanged(SensorEvent se) {
    float x = se.values[0];
    float y = se.values[1];
    float z = se.values[2];
    accelLast = accelCurrent;
    accelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
    float delta = accelCurrent - accelLast;
    accel = accel * 0.9f + delta; // perform low-cut filter
    if (accel > 8) {
      changeAnswer();
    }
  }

  private void changeAnswer() {
    pithySaying.setText(RandomSaying.getInstance().getRandomSaying().getText());
    ObjectAnimator colorFade = ObjectAnimator.ofObject(pithySaying, "textColor", new ArgbEvaluator(),
        ContextCompat.getColor(this, R.color.colorAccent),
        ContextCompat.getColor(this, R.color.colorPrimaryDark));
    colorFade.setDuration(FADE_DURATION);
    colorFade.start();
  }

  @Override
  public void onClick(View v) {
    changeAnswer();
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // Do Nothing
  }

}
