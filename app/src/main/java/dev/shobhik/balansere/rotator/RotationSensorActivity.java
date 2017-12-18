package dev.shobhik.balansere.rotator;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import dev.shobhik.balansere.R;
import dev.shobhik.balansere.data.LocalData;

/** Accepts Intent extras "precision" and "returnType"
 *
 * Precision is the number of decimal places to display and save
 * Return type is either "boolean" or "float"
 * if return type is boolean, accept another extra, "boolPrecision" which is how many decimal places to compare against.
 * This can be smaller than precision, so less accurate values can be accepted as true
 *
 */
public class RotationSensorActivity extends AppCompatActivity implements Orientation.Listener {

    //Views
    private Orientation mOrientation;
    private AttitudeIndicator mAttitudeIndicator;
    TextView tvInclinationValue;
    Button calibrateBtn, resetBtn, saveBtn;

    //Adjustment variables
    float offsetAmount = 0.0f;
    int precisionDigits = 2;
    String returnType = "";
    int boolPrecision = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context mContext = this;
        //View Declarations
        setContentView(R.layout.rotation_sensor_activity);
        tvInclinationValue = findViewById(R.id.tv_inclination_value);
        calibrateBtn = findViewById(R.id.calibrate_button);
        resetBtn = findViewById(R.id.calibration_reset_button);
        saveBtn = findViewById(R.id.save_button);
        mOrientation = new Orientation(this);
        mAttitudeIndicator = (AttitudeIndicator) findViewById(R.id.attitude_indicator);

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int pxWidth = displayMetrics.widthPixels;
        int widthdiff = pxWidth % 100;
        int idealFrameSize = pxWidth - widthdiff;

        ViewGroup.LayoutParams params = mAttitudeIndicator.getLayoutParams();
        params.width = idealFrameSize;
        params.height = idealFrameSize;
        FrameLayout.LayoutParams resizedParams = new FrameLayout.LayoutParams(idealFrameSize, idealFrameSize, Gravity.CENTER_HORIZONTAL);
        mAttitudeIndicator.setLayoutParams(resizedParams);

//        Log.v("Rotator", "New size: " + idealFrameSize + " | " + params.width +  " | " + params2.width + " | " + mAttitudeIndicator.getLayoutParams().width + " | " + mAttitudeIndicator.getWidth());



//        offsetAmount  = Float.parseFloat(LocalData.getString(mContext, LocalData.CALIBRATION_VALUE, "0.0"));
        offsetAmount  = LocalData.getFloat(mContext, LocalData.CALIBRATION_VALUE_FLOAT);
        mAttitudeIndicator.setCalibration(offsetAmount);
        Intent incomingIntent = getIntent();
        if(incomingIntent.hasExtra("precision")) {
            precisionDigits = incomingIntent.getIntExtra("precision", 1);
        }
        if(incomingIntent.hasExtra("returnType")) {
            returnType = incomingIntent.getStringExtra("returnType");
        }
        if(returnType.equalsIgnoreCase("boolean") && incomingIntent.hasExtra("boolPrecision")) {
            boolPrecision = incomingIntent.getIntExtra("boolPrecision", 2);
        }



        calibrateBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            offsetAmount  = mAttitudeIndicator.getInclindation();//Float.parseFloat(tvInclinationValue.getText().toString());
            mAttitudeIndicator.setCalibration(offsetAmount);
//            LocalData.setString(mContext, LocalData.CALIBRATION_VALUE, tvInclinationValue.getText().toString());
            LocalData.setFloat(mContext, LocalData.CALIBRATION_VALUE_FLOAT, offsetAmount);
        Log.v("Calibrator", "New amount: " + offsetAmount + " | " + tvInclinationValue.getText().toString() + " | " + mAttitudeIndicator.getInclindation());
          }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mAttitudeIndicator.setCalibration(0.0f);
//            LocalData.setString(mContext, LocalData.CALIBRATION_VALUE, "0.0");
              LocalData.setFloat(mContext, LocalData.CALIBRATION_VALUE_FLOAT, 0.0f);
              offsetAmount  = LocalData.getFloat(mContext, LocalData.CALIBRATION_VALUE_FLOAT);
              Log.v("Calibrator", "Reset amount: " + offsetAmount + " | " + tvInclinationValue.getText().toString());

          }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
//              Toast.makeText(mContext,  "Eval: " + evaluateBoolean(), Toast.LENGTH_SHORT );
//              Log.v("Evaluator", "Testing: :" + calculateInclination());
//              boolPrecision = 0;
//              Log.v("Evaluator", "Eval " + boolPrecision + ": :" + evaluateBoolean());
//              boolPrecision = 1;
//              Log.v("Evaluator", "Eval " + boolPrecision + ": :" + evaluateBoolean());
//              boolPrecision = 2;
//              Log.v("Evaluator", "Eval " + boolPrecision + ": :" + evaluateBoolean());
//              boolPrecision = 3;
//              Log.v("Evaluator", "Eval " + boolPrecision + ": :" + evaluateBoolean());
//              boolPrecision = 4;
//              Log.v("Evaluator", "Eval " + boolPrecision + ": :" + evaluateBoolean());

              Intent returnIntent = new Intent();
              if(returnType.equalsIgnoreCase("boolean")) {
                  returnIntent.putExtra("isLevel", evaluateBoolean());
              } else {
                  returnIntent.putExtra("value", calculateInclination());
              }
              setResult(RESULT_OK, returnIntent);
              finish();

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
        String display = String.format(getDecimalFormat(), (pitch + 90 - offsetAmount));
        tvInclinationValue.setText("" + (display) );
    }

    private String getDecimalFormat() {
        switch (precisionDigits) {
            case 0:
                return "%.0f";
            case 1:
                return "%.1f";
            case 2:
                return "%.2f";
            case 3:
                return "%.3f";
            case 4:
                return "%.4f";
            default:
                return "%.1f";
        }
    }

    private boolean evaluateBoolean() {
        boolean validity;
        float inclinationValue = calculateInclination();
        switch (boolPrecision) {
            case 0:
                int value = Math.round(calculateInclination());
                validity = (value == 0);
                break;
            case 1:
                validity = (inclinationValue < 0.1);
                break;
            case 2:
                validity = (inclinationValue < 0.01);
                break;

            case 3:
                validity = (inclinationValue < 0.001);
                break;

            case 4:
                validity = (inclinationValue < 0.0001);
                break;

            default:
                validity = (inclinationValue < 0.01);
                break;
        }

        return validity;
    }

    private float calculateInclination() {
        return Math.abs(mAttitudeIndicator.getInclindation());
    }


}
