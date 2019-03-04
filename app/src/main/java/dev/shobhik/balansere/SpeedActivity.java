package dev.shobhik.balansere;


import java.util.Formatter;
import java.util.Locale;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import dev.shobhik.balansere.data.LocalData;


public class SpeedActivity extends AppCompatActivity implements IBaseGpsListener {

    Context mContext;
    private static final String TAG = "SpeedActivity";
    private SensorManager mAccelSensorManager;
    private Sensor mAccelSensor;
    private Sensor mMagneticSensor;
    private final float[] mAccelerometerReading = new float[3];
    TextView accelText;
    Button calibrateButton;
    Button resetButton;
    GraphView graphView;
    LocationManager locationManager;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    double lastX1Value = 1d;
    double lastX2Value = 1d;
    float offsetAmount = 0.0f;
    float milesToMetersOffsetAmount = 1f;
    double currentval = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.speed_activity);

        mAccelSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelSensor = mAccelSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelText = findViewById(R.id.speed_text_accel);
        offsetAmount  = LocalData.getFloat(mContext, LocalData.CALIBRATION_SPEED_FLOAT);
        calibrateButton = findViewById(R.id.speed_calibrate_btn);
        resetButton = findViewById(R.id.speed_reset_btn);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offsetAmount  = (float) currentval;
                LocalData.setFloat(mContext, LocalData.CALIBRATION_SPEED_FLOAT, offsetAmount);
                Log.v("Calibrator", "New amount: " + offsetAmount);
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalData.setFloat(mContext, LocalData.CALIBRATION_SPEED_FLOAT, 0.0f);
                offsetAmount  = LocalData.getFloat(mContext, LocalData.CALIBRATION_SPEED_FLOAT);
                Log.v("Calibrator", "Reset amount: " + offsetAmount);

            }
        });
        graphView = (GraphView) findViewById(R.id.speed_graph);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(40);
        graphView.setTitleColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        mSeries1 = new LineGraphSeries<>();
        mSeries2 = new LineGraphSeries<>();
        mSeries1.setColor(ContextCompat.getColor(mContext, R.color.colorAccentDark));
        mSeries2.setColor(ContextCompat.getColor(mContext, R.color.colorAccentDark));
        graphView.addSeries(mSeries1);
        graphView.addSeries(mSeries2);

        GridLabelRenderer renderer = graphView.getGridLabelRenderer();
        renderer.setGridColor(ContextCompat.getColor(mContext, R.color.colorHighlight));
        renderer.setHorizontalLabelsColor(ContextCompat.getColor(mContext, R.color.colorHighlight));
        renderer.setVerticalLabelsColor(ContextCompat.getColor(mContext, R.color.colorHighlight));

        CheckBox checkUseMetricOffset = (CheckBox) this.findViewById(R.id.speed_checkbox_offset);
        checkUseMetricOffset.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked) {
                    milesToMetersOffsetAmount = 3.6f;
                } else {
                    milesToMetersOffsetAmount = 1.0f;
                }
            }
        });
        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.speed_checkbox_metric);
        chkUseMetricUnits.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                updateSpeed(null);
            }
        });
        chkUseMetricUnits.setChecked(true);

        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION},
                CONSTANTS.ASK_PERMISSIONS);
    }

    @Override
    protected void onResume() {
        super.onResume();


        mAccelSensorManager.registerListener(mListener, mAccelSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccelSensorManager.registerListener(mListener, mMagneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause: ");
        if(locationManager !=null) {
            Log.v(TAG, "onPause: YES");
            locationManager.removeUpdates(this);
        } else {
            try{
                locationManager.removeUpdates(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(mAccelSensorManager != null) {
            mAccelSensorManager.unregisterListener(mListener);
        }

    }

    private SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == mAccelSensor) {
                System.arraycopy(event.values, 0, mAccelerometerReading,
                        0, mAccelerometerReading.length);
                double readX = (mAccelerometerReading[0]);
                double readY = (mAccelerometerReading[1]);
                double readZ = (mAccelerometerReading[2]);
//                float readZ = Math.abs(mAccelerometerReading[2]-9.8f);
                double val = Math.sqrt((readX * readX) + (readY * readY) + (readZ * readZ));
                val = Math.abs(val - offsetAmount);
                currentval = val;
                mSeries2.appendData(new DataPoint(lastX2Value, val), true, 40);
                String strUnits = " m/s";
                if(!useMetricUnits())
                {
                    //Convert meters/second to miles/hour
                    val = val * 2.2369362920544f/milesToMetersOffsetAmount;
                    strUnits = " mph";
                }

                lastX2Value += 1d;
                String strValue = String.format(Locale.US, "%.2f", val) + strUnits;
                String strX = String.format(Locale.US, "%.2f", readX);
                String strY = String.format(Locale.US, "%.2f", readY);
                String strZ = String.format(Locale.US, "%.2f", readZ);
                accelText.setText(strValue);
                Log.v(TAG, "Acc: " + val + " : " + strX + "," + strY + "," + strZ + " | " + offsetAmount);

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void runLocationUpdates() {
        Log.e("LocationUtils", "runLocationUpdates() A");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Log.e("LocationUtils", "runLocationUpdates() B");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateSpeed(null);
    }


    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("LocationUtils", "requestLocationPermission() A");
            requestPermissions(new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    CONSTANTS.ASK_PERMISSIONS);
        } else {
            Log.e("LocationUtils", "requestLocationPermission() A");
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION},
                    CONSTANTS.ASK_PERMISSIONS);
        }
    }

    public boolean hasLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
                Log.e("LocationUtils", "hasLocationPermission() A");
                return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
            } else {
                Log.e("LocationUtils", "hasLocationPermission() A");
                return (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    public boolean shouldShowRequestLocationDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("LocationUtils", "shouldShowRequestLocationDialog() A");
            return shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            Log.e("LocationUtils", "shouldShowRequestLocationDialog() B");
            return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CONSTANTS.ASK_PERMISSIONS:
                if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && hasLocationPermission()) {
                    Log.i(TAG, "Location Permission Granted");
                    runLocationUpdates();

                } else {
                    Log.e(TAG, "Location Permission Denied");
                    if (shouldShowRequestLocationDialog()) {
                        showRequestLocationDialog();
                    } /*else { //shows location-denied warning even if
                               // user checks 'don't show again'
                        showLocationDeniedWarning();
                    }*/
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showRequestLocationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cannot Access Location")
                .setPositiveButton("Yes, grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestLocationPermission();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showLocationDeniedWarning();
                    }
                })
                .show();
    }

    public void showLocationDeniedWarning() {
        new AlertDialog.Builder(this)
                .setTitle("Unable to Obtain Location Data")
                .setNeutralButton("Got it", null)
                .show();
    }


    public void finish()
    {
        super.finish();
//        System.exit(0);
    }

    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;

        if(location != null)
        {
            location.setUseMetricunits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        lastX1Value += 1d;
        mSeries1.appendData(new DataPoint(lastX1Value, nCurrentSpeed), true, 40);
        String strUnits = "mph";
        if(this.useMetricUnits())
        {
            strUnits = "m/s";
        }
        TextView txtCurrentSpeed = (TextView) this.findViewById(R.id.speed_text_location);
        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
    }

    private boolean useMetricUnits() {
        // TODO Auto-generated method stub
        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.speed_checkbox_metric);
        return chkUseMetricUnits.isChecked();
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if(location != null)
        {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub

    }



}