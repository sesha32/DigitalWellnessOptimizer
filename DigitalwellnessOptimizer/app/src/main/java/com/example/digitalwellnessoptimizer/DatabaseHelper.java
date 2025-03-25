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
    private static final int DATABASE_VERSION = 2; // Increased version

    // Table Names
    public static final String TABLE_APP_USAGE = "app_usage";
    public static final String TABLE_APP_CATEGORY = "app_category";

    // Column Names for app_usage table
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_APP_NAME = "app_name"; // Store package name instead
    public static final String COLUMN_USAGE_TIME = "usage_time";
    public static final String COLUMN_LAST_OPENED = "last_opened";
    public static final String COLUMN_DATE = "date";

    // Column Names for app_category table
    public static final String COLUMN_PACKAGE_NAME = "package_name";
    public static final String COLUMN_CATEGORY = "category"; // "productive" or "non-productive"

    // Create Table Queries
    private static final String CREATE_TABLE_APP_USAGE =
            "CREATE TABLE " + TABLE_APP_USAGE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_APP_NAME + " TEXT, " +
                    COLUMN_USAGE_TIME + " INTEGER, " +
                    COLUMN_LAST_OPENED + " TEXT, " +
                    COLUMN_DATE + " TEXT);";

    private static final String CREATE_TABLE_APP_CATEGORY =
            "CREATE TABLE " + TABLE_APP_CATEGORY + " (" +
                    COLUMN_PACKAGE_NAME + " TEXT PRIMARY KEY, " +
                    COLUMN_CATEGORY + " TEXT);";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP_USAGE);
        db.execSQL(CREATE_TABLE_APP_CATEGORY);
        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_TABLE_APP_CATEGORY);
            insertDefaultCategories(db);
        }
    }

    // Insert Default App Categories
    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] productiveApps = {
                "com.google.android.docs", "com.google.android.apps.docs",
                "com.microsoft.office.word", "com.microsoft.office.excel",
                "com.google.android.gm", "com.android.chrome"
        };

        String[] nonProductiveApps = {
                "com.instagram.android", "com.facebook.katana",
                "com.snapchat.android", "com.tiktok.android",
                "com.netflix.mediaclient", "com.google.android.youtube"
        };

        for (String packageName : productiveApps) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PACKAGE_NAME, packageName);
            values.put(COLUMN_CATEGORY, "productive");
            db.insert(TABLE_APP_CATEGORY, null, values);
        }

        for (String packageName : nonProductiveApps) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PACKAGE_NAME, packageName);
            values.put(COLUMN_CATEGORY, "non-productive");
            db.insert(TABLE_APP_CATEGORY, null, values);
        }
    }

    // Insert or Update App Category
    public void insertOrUpdateAppCategory(String packageName, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PACKAGE_NAME, packageName);
        values.put(COLUMN_CATEGORY, category);

        db.insertWithOnConflict(TABLE_APP_CATEGORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Get App Category
    public String getAppCategory(String packageName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APP_CATEGORY, new String[]{COLUMN_CATEGORY},
                COLUMN_PACKAGE_NAME + " = ?", new String[]{packageName},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String category = cursor.getString(0);
            cursor.close();
            db.close();
            return category;
        }
        db.close();
        return "unknown"; // Default category if not found
    }

    // Check if an app is non-productive
    public boolean isNonProductiveApp(String packageName) {
        return getAppCategory(packageName).equals("non-productive");
    }

    // Insert App Usage Data
    public void insertAppUsage(String packageName, long usageTime, String lastOpened, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_NAME, packageName);
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

                // Retrieve app category
                String category = getAppCategory(packageName);

                // Retrieve app icon dynamically
                Drawable appIcon = getAppIcon(packageName);

                appUsageList.add(new AppUsageModel(packageName, usageTime, lastOpened, date, appIcon, category));
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
            return context.getDrawable(R.drawable.default_app_icon);
        }
    }
}
