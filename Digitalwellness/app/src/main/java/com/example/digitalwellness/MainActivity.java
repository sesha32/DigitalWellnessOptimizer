package com.example.digitalwellness;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsage;
    private AppUsageAdapter appUsageAdapter;
    private TextView textSleepData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewUsage = findViewById(R.id.recyclerViewUsage);
        textSleepData = findViewById(R.id.textSleepData);

        // Setting up RecyclerView
        recyclerViewUsage.setLayoutManager(new LinearLayoutManager(this));
        List<String> appUsageData = getAppUsageData();
        appUsageAdapter = new AppUsageAdapter(appUsageData);
        recyclerViewUsage.setAdapter(appUsageAdapter);

        // Hardcoded sleep tracking data
        textSleepData.setText("Sleep Duration: 7 hours\nSleep Quality: Excellent");

        // Show demo alert for app overuse
        showUsageAlert(appUsageData);
    }

    private List<String> getAppUsageData() {
        return Arrays.asList(
                "YouTube - 2 hours",
                "WhatsApp - 40 mins",
                "Google - 25 mins",
                "Bubble Rainbow - 21 mins",
                "Google Chrome - 5 mins",
                "Chatgpt.com - 4 mins",
                "Gmail - 4 mins",
                "Eenadu.net - 2 mins",
                "Tiny.jio.com - 1 min",
                "Settings - 1 min"
        );
    }

    private void showUsageAlert(List<String> appUsageData) {
        // Identify the most used app
        String overusedApp = appUsageData.get(0); // Assuming the list is sorted by usage

        new AlertDialog.Builder(this)
                .setTitle("Reduce App Usage")
                .setMessage("You have been using " + overusedApp + " too much! Try taking a break.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
