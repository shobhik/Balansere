package dev.shobhik.balansere;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Shobhik Ghosh on 11/21/2017.
 */

public class SensorActivityBackup extends Activity {

    private static final String TAG = "SensorActivity";
    private SensorManager mAccelSensorManager;
    private Sensor mAccelSensor;
    private Sensor mMagneticSensor;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    private final float[] mAccelAveragesX = new float[2];
    private final float[] mAccelAveragesY = new float[2];
    private final float[] mAccelAveragesZ = new float[2];
    private final float[] mMagAveragesX = new float[2];
    private final float[] mMagAveragesY = new float[2];
    private final float[] mMagAveragesZ = new float[2];

    Button button;
    TextView textAccelX;
    TextView textAccelY;
    TextView textAccelZ;
    TextView textMagX;
    TextView textMagY;
    TextView textMagZ;
    TextView textAccelAveX;
    TextView textAccelAveY;
    TextView textAccelAveZ;
    TextView textMagAveX;
    TextView textMagAveY;
    TextView textMagAveZ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        mAccelSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelSensor = mAccelSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        mMagneticSensor = mAccelSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mMagneticSensor = mAccelSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        button = findViewById(R.id.sensor_button);
        textAccelX = findViewById(R.id.sensor_text_x);
        textAccelY = findViewById(R.id.sensor_text_y);
        textAccelZ = findViewById(R.id.sensor_text_z);
        textMagX = findViewById(R.id.sensor_mag_text_x);
        textMagY = findViewById(R.id.sensor_mag_text_y);
        textMagZ = findViewById(R.id.sensor_mag_text_z);
        textAccelAveX = findViewById(R.id.sensor_accel_text_ave_x);
        textAccelAveY = findViewById(R.id.sensor_accel_text_ave_y);
        textAccelAveZ = findViewById(R.id.sensor_accel_text_ave_z);
        textMagAveX = findViewById(R.id.sensor_mag_text_ave_x);
        textMagAveY = findViewById(R.id.sensor_mag_text_ave_y);
        textMagAveZ = findViewById(R.id.sensor_mag_text_ave_z);
        mAccelAveragesX[0] = 0.0f;
        mAccelAveragesY[0] = 0.0f;
        mAccelAveragesZ[0] = 0.0f;
        mAccelAveragesX[1] = 0.0f;
        mAccelAveragesY[1] = 0.0f;
        mAccelAveragesZ[1] = 0.0f;
        mMagAveragesX[0] = 0.0f;
        mMagAveragesY[0] = 0.0f;
        mMagAveragesZ[0] = 0.0f;
        mMagAveragesX[1] = 0.0f;
        mMagAveragesY[1] = 0.0f;
        mMagAveragesZ[1] = 0.0f;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        mAccelSensorManager.registerListener(mListener, mAccelSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccelSensorManager.registerListener(mListener, mMagneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any more updates from either sensor.
        mAccelSensorManager.unregisterListener(mListener);
    }

    private SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == mAccelSensor) {
                System.arraycopy(event.values, 0, mAccelerometerReading,
                        0, mAccelerometerReading.length);
                String result = printValues(mAccelerometerReading);
                textAccelX.setText(""+mAccelerometerReading[0]);
                textAccelY.setText(""+mAccelerometerReading[1]);
                textAccelZ.setText(""+mAccelerometerReading[2]);
                Log.v(TAG, "Acc: " + result);
                updateAverages(mAccelAveragesX, mAccelerometerReading[0]);
                updateAverages(mAccelAveragesY, mAccelerometerReading[1]);
                updateAverages(mAccelAveragesZ, mAccelerometerReading[2]);
                textAccelAveX.setText("" + mAccelAveragesX[1]);
                textAccelAveY.setText("" + mAccelAveragesY[1]);
                textAccelAveZ.setText("" + mAccelAveragesZ[1]);

            }
            else if (event.sensor == mMagneticSensor) {
                System.arraycopy(event.values, 0, mMagnetometerReading,
                        0, mMagnetometerReading.length);
                String magresult = printValues(mAccelerometerReading);
                textMagX.setText(""+mMagnetometerReading[0]);
                textMagY.setText(""+mMagnetometerReading[1]);
                textMagZ.setText(""+mMagnetometerReading[2]);
                Log.v(TAG, "Mag: " + magresult);
                updateAverages(mMagAveragesX, mMagnetometerReading[0]);
                updateAverages(mMagAveragesY, mMagnetometerReading[1]);
                updateAverages(mMagAveragesZ, mMagnetometerReading[2]);
                textMagAveX.setText("" + mMagAveragesX[1]);
                textMagAveY.setText("" + mMagAveragesY[1]);
                textMagAveZ.setText("" + mMagAveragesZ[1]);

                textAccelAveX.setText("" + mAccelAveragesX[1]);
                textAccelAveY.setText("" + mAccelAveragesY[1]);
                textAccelAveZ.setText("" + mAccelAveragesZ[1]);

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private String printValues(float[] array) {
        String result = "";
        for(float pos : array) {
            result += (pos + ",");
        }
        if(result.length() > 0) {
            result = result.substring(0, result.length()-2);
        }
        return result;
    }


    private void updateAverages(float[] averageHolder, float newValue){
        float size = averageHolder[0];
        size += 1;
//        averageHolder[1] = (averageHolder[1] + newValue)/size;
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mAccelSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        mAccelSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }




}