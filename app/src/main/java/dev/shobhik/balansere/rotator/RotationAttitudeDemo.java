package dev.shobhik.balansere.rotator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
    setContentView(R.layout.main);

    mOrientation = new Orientation(this);
    mAttitudeIndicator = (AttitudeIndicator) findViewById(R.id.attitude_indicator);

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
