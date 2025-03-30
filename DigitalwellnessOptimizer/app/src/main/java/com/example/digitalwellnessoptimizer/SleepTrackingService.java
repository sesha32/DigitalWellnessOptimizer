package com.example.digitalwellnessoptimizer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SleepTrackingService extends Service {
    private static final String CHANNEL_ID = "SleepTrackingChannel";
    private Handler handler = new Handler();
    private Runnable sleepTrackingTask;
    private PowerManager powerManager;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Initialize components
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Start foreground service with notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleep Tracking")
                .setContentText("Tracking your sleep patterns...")
                .setSmallIcon(R.drawable.ic_sleep)
                .build();
        startForeground(1, notification);

        // Define task to monitor sleep
        sleepTrackingTask = new Runnable() {
            @Override
            public void run() {
                trackSleep();
                handler.postDelayed(this, 10 * 60 * 1000); // Check every 10 minutes
            }
        };

        handler.post(sleepTrackingTask);
    }

    private void trackSleep() {
        Log.d("SleepTrackingService", "Checking for sleep patterns...");

        boolean isScreenOn = powerManager.isInteractive(); // Returns false if screen is off

        // Store in database
        DatabaseHelper db = new DatabaseHelper(this);
        db.insertSleepData(System.currentTimeMillis(), isScreenOn ? "Awake" : "Sleeping");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sleep Tracking Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Restart service if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sleepTrackingTask);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
