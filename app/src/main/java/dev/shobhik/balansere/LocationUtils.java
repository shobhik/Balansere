package dev.shobhik.balansere;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

public class LocationUtils extends AppCompatActivity {

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


}
