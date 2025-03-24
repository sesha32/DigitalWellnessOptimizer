package com.example.digitalwellnessoptimizer;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        // Set package name instead of app name
        holder.appNameTextView.setText(appUsage.getPackageName());

        // Format and set usage time
        holder.usageTimeTextView.setText("Usage Time: " + formatUsageTime(appUsage.getUsageTime()));

        // Set last opened time
        holder.lastOpenedTextView.setText("Last Opened: " + appUsage.getLastOpened());

        // Set date information
        holder.dateTextView.setText("Date: " + appUsage.getDate());

        // Set App Icon with fallback
        Drawable appIcon = appUsage.getAppIcon();
        if (appIcon != null) {
            holder.appIconImageView.setImageDrawable(appIcon);
        } else {
            holder.appIconImageView.setImageResource(R.drawable.default_app_icon); // Fallback icon
        }
    }

    @Override
    public int getItemCount() {
        return appUsageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView appNameTextView, usageTimeTextView, lastOpenedTextView, dateTextView;
        ImageView appIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.text_app_name);
            usageTimeTextView = itemView.findViewById(R.id.text_usage_time);
            lastOpenedTextView = itemView.findViewById(R.id.text_last_opened);
            dateTextView = itemView.findViewById(R.id.text_date);
            appIconImageView = itemView.findViewById(R.id.app_icon);
        }
    }

    // Helper method to format usage time in HH:MM:SS
    private String formatUsageTime(long usageTime) {
        int seconds = (int) (usageTime / 1000) % 60;
        int minutes = (int) ((usageTime / (1000 * 60)) % 60);
        int hours = (int) ((usageTime / (1000 * 60 * 60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
