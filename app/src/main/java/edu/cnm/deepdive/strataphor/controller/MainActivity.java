package edu.cnm.deepdive.strataphor.controller;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.view.ViewGroup;
import android.widget.TextView;
import edu.cnm.deepdive.android.BaseFluentAsyncTask;
import edu.cnm.deepdive.strataphor.R;
import edu.cnm.deepdive.strataphor.model.StratAphorDatabase;
import edu.cnm.deepdive.strataphor.model.pojo.SayingWithSource;

public class MainActivity extends AppCompatActivity implements SensorEventListener,
    OnClickListener {

  private static final int FADE_DURATION = 8000;

  private TextView sayingText;
  private TextView sourceName;
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
    sayingText = findViewById(R.id.saying_text);
    sourceName = findViewById(R.id.source_name);
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
    new BaseFluentAsyncTask<Void, Void, SayingWithSource, SayingWithSource>()
        .setPerformer((ignore) ->
            StratAphorDatabase.getInstance().getSayingDao().findFirstRandom())
        .setSuccessListener((sayingWithSource) -> {
          sayingText.setText(sayingWithSource.getSaying());
          sourceName.setText(sayingWithSource.getSource());
          fadeTogether(sayingText, sourceName);
        })
        .execute();
  }

  @Override
  public void onClick(View v) {
    changeAnswer();
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // Do Nothing
  }

  private void fadeTogether(TextView... textViews) {
    ObjectAnimator[] animators = new ObjectAnimator[textViews.length];
    for (int i = 0; i < textViews.length; i++) {
      ObjectAnimator fade =
          (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.text_fade);
      fade.setTarget(textViews[i]);
      animators[i] = fade;
    }
    AnimatorSet set = new AnimatorSet();
    set.playTogether(animators);
    set.start();
  }
}
