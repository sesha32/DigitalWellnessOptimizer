package com.example.digitalwellnessoptimizer;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalwellnessoptimizer.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private AppUsageAdapter adapter;
    private FloatingActionButton fab;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Ensure correct initialization of NavController
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Initialize Database
        databaseHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display app usage data
        fetchAndStoreAppUsage();

        // Floating Action Button (FAB) - Navigate to AppUsageFragment
        fab = binding.fab;
        fab.setOnClickListener(view -> navController.navigate(R.id.appUsageFragment));

        // Check and request Usage Access permission
        if (!hasUsageAccessPermission()) {
            requestUsageAccessPermission();
        } else {
            Toast.makeText(this, "Usage Access Permission Granted", Toast.LENGTH_SHORT).show();
        }

        // Start app usage monitoring service
        startService(new Intent(this, AppUsageMonitorService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private boolean hasUsageAccessPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestUsageAccessPermission() {
        Toast.makeText(this, "Please enable Usage Access for better tracking", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void fetchAndStoreAppUsage() {
        List<UsageStats> usageStatsList = UsageStatsHelper.getAppUsageStats(this);
        PackageManager packageManager = getPackageManager();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        List<AppUsageModel> appUsageList = databaseHelper.getAllAppUsage(); // Initialize list

        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();
            long usageTime = usageStats.getTotalTimeInForeground();
            String lastOpened = dateFormat.format(new Date(usageStats.getLastTimeUsed()));
            String date = dateFormat.format(new Date());

            Drawable appIcon;
            try {
                appIcon = packageManager.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                appIcon = getDrawable(R.drawable.default_app_icon); // Fallback icon
            }

            String category = databaseHelper.getAppCategory(packageName); // ✅ Get app category

            databaseHelper.insertAppUsage(packageName, usageTime, lastOpened, date);
            appUsageList.add(new AppUsageModel(packageName, usageTime, lastOpened, date, appIcon, category));
        }

        // Refresh RecyclerView
        adapter = new AppUsageAdapter(this, appUsageList);
        recyclerView.setAdapter(adapter);
    }

    // Handle notification permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
