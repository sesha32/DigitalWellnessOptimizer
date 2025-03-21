package com.example.digitalwellnessoptimizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {
    private List<AppUsageModel> appUsageList;

    public AppUsageAdapter(List<AppUsageModel> appUsageList) {
        this.appUsageList = appUsageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_usage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUsageModel appUsage = appUsageList.get(position);
        holder.appNameTextView.setText(appUsage.getAppName());
        holder.usageTimeTextView.setText("Usage Time: " + formatTime(appUsage.getUsageTime()));
        holder.lastOpenedTextView.setText("Last Opened: " + appUsage.getLastOpened());
        holder.dateTextView.setText("Date: " + appUsage.getDate());
    }

    @Override
    public int getItemCount() {
        return appUsageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView appNameTextView, usageTimeTextView, lastOpenedTextView, dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.text_app_name);
            usageTimeTextView = itemView.findViewById(R.id.text_usage_time);
            lastOpenedTextView = itemView.findViewById(R.id.text_last_opened);
            dateTextView = itemView.findViewById(R.id.text_date);
        }
    }

    // Helper method to format usage time in HH:MM:SS
    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
