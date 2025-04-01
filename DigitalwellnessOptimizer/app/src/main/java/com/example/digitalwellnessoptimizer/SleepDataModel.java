package com.example.digitalwellnessoptimizer;

public class SleepDataModel {
    private long timestamp;
    private String sleepStatus;

    public SleepDataModel(long timestamp, String sleepStatus) {
        this.timestamp = timestamp;
        this.sleepStatus = sleepStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSleepStatus() {
        return sleepStatus;
    }
}
