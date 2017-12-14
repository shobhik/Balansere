package dev.shobhik.balansere.rotator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import dev.shobhik.balansere.R;

public class RotationAttitudeDemo extends AppCompatActivity implements Orientation.Listener {

  private Orientation mOrientation;
  private AttitudeIndicator mAttitudeIndicator;
  TextView tvX, tvY, tvZ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mOrientation = new Orientation(this);
    mAttitudeIndicator = (AttitudeIndicator) findViewById(R.id.attitude_indicator);

    tvX = findViewById(R.id.tv_x);
    tvY = findViewById(R.id.tv_y);
    tvZ = findViewById(R.id.tv_z);
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
    tvX.setText("" + pitch);
    tvY.setText("" + roll);
    tvZ.setText("" + (pitch + 90) );
  }
}
