package com.example.digitalwellnessoptimizer;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "digital_wellness.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    public static final String TABLE_APP_USAGE = "app_usage";

    // Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_APP_NAME = "app_name"; // Store package name instead
    public static final String COLUMN_USAGE_TIME = "usage_time";
    public static final String COLUMN_LAST_OPENED = "last_opened";
    public static final String COLUMN_DATE = "date";

    // Create Table Query
    private static final String CREATE_TABLE_APP_USAGE =
            "CREATE TABLE " + TABLE_APP_USAGE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_APP_NAME + " TEXT, " +
                    COLUMN_USAGE_TIME + " INTEGER, " +
                    COLUMN_LAST_OPENED + " TEXT, " +
                    COLUMN_DATE + " TEXT);";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP_USAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_USAGE);
        onCreate(db);
    }

    // Insert App Usage Data
    public void insertAppUsage(String packageName, long usageTime, String lastOpened, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_NAME, packageName); // Store package name
        values.put(COLUMN_USAGE_TIME, usageTime);
        values.put(COLUMN_LAST_OPENED, lastOpened);
        values.put(COLUMN_DATE, date);

        db.insert(TABLE_APP_USAGE, null, values);
        db.close();
    }

    // Retrieve All App Usage Data
    public List<AppUsageModel> getAllAppUsage() {
        List<AppUsageModel> appUsageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_APP_USAGE, null);
        if (cursor.moveToFirst()) {
            do {
                String packageName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_NAME));
                long usageTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USAGE_TIME));
                String lastOpened = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_OPENED));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));

                // Retrieve app icon dynamically
                Drawable appIcon = getAppIcon(packageName);

                appUsageList.add(new AppUsageModel(packageName, usageTime, lastOpened, date, appIcon));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return appUsageList;
    }

    // Helper method to get app icon from package name
    private Drawable getAppIcon(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationIcon(appInfo);
        } catch (PackageManager.NameNotFoundException e) {
            // Return a default icon if not found
            return context.getDrawable(R.drawable.default_app_icon);
        }
    }
}
