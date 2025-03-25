package com.example.digitalwellnessoptimizer;

import android.graphics.drawable.Drawable;

public class AppUsageModel {
    private String packageName;
    private long usageTime;
    private String lastOpened;
    private String date;
    private Drawable appIcon;
    private String category; // ✅ Added category field

    public AppUsageModel(String packageName, long usageTime, String lastOpened, String date, Drawable appIcon, String category) {
        this.packageName = packageName;
        this.usageTime = usageTime;
        this.lastOpened = lastOpened;
        this.date = date;
        this.appIcon = appIcon;
        this.category = category;
    }

    public String getPackageName() {
        return packageName;
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

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getCategory() { // ✅ Getter for category
        return category;
    }
}
