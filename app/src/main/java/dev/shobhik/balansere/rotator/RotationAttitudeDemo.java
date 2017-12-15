package dev.shobhik.balansere.rotator;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import dev.shobhik.balansere.R;

public class RotationAttitudeDemo extends AppCompatActivity implements Orientation.Listener {

  private Orientation mOrientation;
  private AttitudeIndicator mAttitudeIndicator;
  TextView tvX, tvY, tvZ;
  Button calibrateBtn, resetBtn;
  float offsetAmount = 0.0f;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Context mContext = this;
    setContentView(R.layout.main);

    Configuration configuration = mContext.getResources().getConfiguration();
    int screenWidthDp = configuration.screenWidthDp;
//    int widthdiff = screenWidthDp % 100;
//    int idealFrameSize = screenWidthDp - widthdiff;
    DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
    float pxHeight = displayMetrics.heightPixels;
    int pxWidth = displayMetrics.widthPixels;
    int widthdiff = pxWidth % 100;
    int idealFrameSize = pxWidth - widthdiff;

    mOrientation = new Orientation(this);
    mAttitudeIndicator = (AttitudeIndicator) findViewById(R.id.attitude_indicator);
    ViewGroup.LayoutParams params = mAttitudeIndicator.getLayoutParams();
    params.width = idealFrameSize;
    params.height = idealFrameSize;
//    FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(params);
    FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(idealFrameSize, idealFrameSize, Gravity.CENTER_HORIZONTAL);
    mAttitudeIndicator.setLayoutParams(params1);
    ViewGroup.LayoutParams params2 = mAttitudeIndicator.getLayoutParams();

    Log.v("Rotator", "New size: " + idealFrameSize + " | " + params.width +  " | " + params2.width + " | " + mAttitudeIndicator.getLayoutParams().width + " | " + mAttitudeIndicator.getWidth());


    tvX = findViewById(R.id.tv_x);
    tvY = findViewById(R.id.tv_y);
    tvZ = findViewById(R.id.tv_z);
    calibrateBtn = findViewById(R.id.calibrate_button);
    resetBtn = findViewById(R.id.calibration_reset_button);

    calibrateBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        offsetAmount  = Float.parseFloat(tvZ.getText().toString());
        mAttitudeIndicator.setCalibration(offsetAmount);
      }
    });
    resetBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mAttitudeIndicator.setCalibration(0.0f);
      }
    });

  }

  @Override
  protected void onStart() {
    super.onStart();
    mOrientation.startListening(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    mOrientation.stopListening();
  }

  @Override
  public void onOrientationChanged(float pitch, float roll) {
    mAttitudeIndicator.setAttitude(pitch, roll);
    tvX.setText("" + (pitch - offsetAmount));
    tvY.setText("" + roll);
    tvZ.setText("" + (pitch + 90 - offsetAmount) );
  }
}
