package dev.shobhik.balansere.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shobhik Ghosh on 1/22/2018.
 */

public class RotationMeasurementDataset {

    private String owner; //Same UUID assigned to device. We can add readable username to BT data later.
    private String measurementSet; //JSON Array String containing all JSON representations of measurements.
    private long createdAt; //Timestamp for creation date;
    private long modifiedAt; //Timestamp for modification date;

    public RotationMeasurementDataset(String owner, String measurementSet) {
        this.owner = owner;
        this.measurementSet = measurementSet;
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = System.currentTimeMillis();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMeasurementSet() {
        return measurementSet;
    }

    public void setMeasurementSet(String measurementSet) {
        this.measurementSet = measurementSet;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    /** Adds a measurement object to the array of measurements. Handles all JSON conversions internally.
     *
     * @param measurement
     */
    public void addMeasurementValue(RotationMeasurement measurement) {
        try {
            JSONArray jsonArray = new JSONArray(this.getMeasurementSet());
            JSONObject jsonObject = new JSONObject(measurement.getMeasurementJson());
            jsonArray.put(jsonObject);
            this.measurementSet = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
