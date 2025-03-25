package com.example.digitalwellnessoptimizer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager; // ✅ Added missing import
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class AppUsageMonitorService extends Service {

    private static final String CHANNEL_ID = "AppUsageChannel";
    private static final long CHECK_INTERVAL = 60000; // 1 minute
    private static final long NON_PRODUCTIVE_LIMIT = 60 * 60 * 1000; // 1 hour in milliseconds

    private Handler handler = new Handler();
    private Runnable usageCheckRunnable;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getForegroundNotification("Monitoring app usage..."));

        dbHelper = new DatabaseHelper(this);

        usageCheckRunnable = new Runnable() {
            @Override
            public void run() {
                checkAppUsage();
                handler.postDelayed(this, CHECK_INTERVAL); // Run every 1 minute
            }
        };
        handler.post(usageCheckRunnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(usageCheckRunnable);
        super.onDestroy();
    }

    private void checkAppUsage() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 24 * 60 * 60 * 1000; // Last 24 hours

        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        if (stats == null || stats.isEmpty()) return;

        SortedMap<Long, UsageStats> sortedStats = new TreeMap<>();
        for (UsageStats usageStat : stats) {
            sortedStats.put(usageStat.getLastTimeUsed(), usageStat);
        }

        if (!sortedStats.isEmpty()) {
            UsageStats currentApp = sortedStats.get(sortedStats.lastKey());
            if (currentApp != null) {
                String packageName = currentApp.getPackageName();
                long usageTime = currentApp.getTotalTimeInForeground();

                String category = dbHelper.getAppCategory(packageName);

                if ("Non-Productive".equals(category) && usageTime > NON_PRODUCTIVE_LIMIT) {
                    sendUsageAlert(packageName + " Usage Alert!", "You've exceeded 1 hour on " + packageName + " today.");
                }
            }
        }
    }

    private void sendUsageAlert(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return; // Don't send notification if permission is denied
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.default_app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notification);
    }

    private Notification getForegroundNotification(String message) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.default_app_icon)
                .setContentTitle("Digital Wellness Optimizer")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "App Usage Monitoring", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
