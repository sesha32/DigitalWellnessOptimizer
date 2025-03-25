package com.example.digitalwellnessoptimizer;

import android.graphics.drawable.Drawable;

public class AppUsageModel {
    private String packageName;
    private long usageTime;
    private String lastOpened;
    private String date;
    private Drawable appIcon;
    private String category;  // ✅ Added category field (productive/non-productive)

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

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(long usageTime) {
        this.usageTime = usageTime;
    }

    public String getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(String lastOpened) {
        this.lastOpened = lastOpened;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getCategory() {  // ✅ Getter for category
        return category;
    }

    public void setCategory(String category) {  // ✅ Setter for category
        this.category = category;
    }
}
