package com.example.digitalwellnessoptimizer;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UsageStatsHelper {

    private static final String TAG = "UsageStatsHelper";

    // Fetch app usage stats for the past day
    public static List<UsageStats> getAppUsageStats(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long endTime = System.currentTimeMillis();
        long startTime = endTime - TimeUnit.DAYS.toMillis(1); // Last 24 hours

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if (usageStatsList == null || usageStatsList.isEmpty()) {
            Log.e(TAG, "No usage stats found");
            return Collections.emptyList();
        }

        // Sort usage stats by time in foreground (most used apps first)
        Collections.sort(usageStatsList, new Comparator<UsageStats>() {
            @Override
            public int compare(UsageStats u1, UsageStats u2) {
                return Long.compare(u2.getTotalTimeInForeground(), u1.getTotalTimeInForeground());
            }
        });

        return usageStatsList;
    }
}
