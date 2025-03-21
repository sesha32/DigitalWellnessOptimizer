package com.example.digitalwellnessoptimizer;

public class AppUsageModel {
    private String appName;
    private long usageTime;
    private String lastOpened;
    private String date;

    public AppUsageModel(String appName, long usageTime, String lastOpened, String date) {
        this.appName = appName;
        this.usageTime = usageTime;
        this.lastOpened = lastOpened;
        this.date = date;
    }

    public String getAppName() {
        return appName;
    }

    public long getUsageTime() {
        return usageTime;
    }

    public String getLastOpened() {
        return lastOpened;
    }

    public String getDate() {
        return date;
    }
}
