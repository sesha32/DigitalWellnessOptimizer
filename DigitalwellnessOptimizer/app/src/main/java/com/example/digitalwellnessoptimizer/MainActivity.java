package com.example.digitalwellnessoptimizer;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalwellnessoptimizer.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper; // Database Helper Instance

    private RecyclerView recyclerView;
    private AppUsageAdapter adapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Initialize Navigation Controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Initialize Database
        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display app usage data
        List<AppUsageModel> appUsageList = dbHelper.getAllAppUsage();
        adapter = new AppUsageAdapter(appUsageList);
        recyclerView.setAdapter(adapter);

        // Floating Action Button (FAB)
        fab = binding.fab;
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(binding.fab)
                .setAction("Action", null).show());

        // Check and request Usage Access permission
        if (!hasUsageAccessPermission()) {
            requestUsageAccessPermission();
        } else {
            Toast.makeText(this, "Usage Access Permission Granted", Toast.LENGTH_SHORT).show();
            fetchAndStoreAppUsage(); // Fetch and store data when permission is granted
        }
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    // Check if Usage Access permission is granted
    private boolean hasUsageAccessPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // Redirect user to Usage Access Settings page
    private void requestUsageAccessPermission() {
        Toast.makeText(this, "Please enable Usage Access for better tracking", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    // Fetch and store app usage data
    private void fetchAndStoreAppUsage() {
        List<UsageStats> usageStatsList = UsageStatsHelper.getAppUsageStats(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        for (UsageStats usageStats : usageStatsList) {
            String appName = usageStats.getPackageName();
            long usageTime = usageStats.getTotalTimeInForeground();
            String lastOpened = dateFormat.format(new Date(usageStats.getLastTimeUsed()));
            String date = dateFormat.format(new Date());

            // Insert into database
            dbHelper.insertAppUsage(appName, usageTime, lastOpened, date);
        }
    }
}