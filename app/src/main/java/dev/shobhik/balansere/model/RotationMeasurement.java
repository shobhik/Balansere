package dev.shobhik.balansere.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shobhik Ghosh on 1/22/2018.
 */

public class RotationMeasurement {

    private String owner; //UUID owner for partnered devices
    private float value; // Calibrated value
    private float calibration; //Calibration value originally applied by sensor device. We can readjust as needed.
    private float order; //Order internal to sensor device. We will sort by this locally.
    private float captureTime; //Timestamp local to device. Use for global ordering.

    public RotationMeasurement(String owner, float value, float calibration, float order) {
        this.owner = owner;
        this.value = value;
        this.calibration = calibration;
        this.order = order;
        this.captureTime = System.currentTimeMillis();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getCalibration() {
        return calibration;
    }

    public void setCalibration(float calibration) {
        this.calibration = calibration;
    }

    public float getOrder() {
        return order;
    }

    public void setOrder(float order) {
        this.order = order;
    }

    public float getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(float captureTime) {
        this.captureTime = captureTime;
    }

    @Override
    public String toString() {
        return "RotationMeasurement{" +
                "owner='" + owner + '\'' +
                ", value=" + value +
                ", calibration=" + calibration +
                ", order=" + order +
                ", captureTime=" + captureTime +
                '}';
    }

    public String getMeasurementJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("owner", owner);
            jsonObject.put("value", value);
            jsonObject.put("calibration", calibration);
            jsonObject.put("order", order);
            jsonObject.put("captureTime", captureTime);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }

    }
}
