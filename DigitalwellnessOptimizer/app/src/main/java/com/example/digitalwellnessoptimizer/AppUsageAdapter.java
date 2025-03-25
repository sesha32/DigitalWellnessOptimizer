package com.example.digitalwellnessoptimizer;

import android.content.Context;
import android.graphics.Color;
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
    private Context context;
    private List<AppUsageModel> appUsageList;

    public AppUsageAdapter(Context context, List<AppUsageModel> appUsageList) {
        this.context = context;
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

        // Set app name
        holder.appNameTextView.setText(appUsage.getPackageName());

        // Set usage time
        holder.usageTimeTextView.setText("Usage: " + formatUsageTime(appUsage.getUsageTime()));

        // Set last opened time
        holder.lastOpenedTextView.setText("Last Opened: " + appUsage.getLastOpened());

        // Set app icon
        Drawable appIcon = appUsage.getAppIcon();
        if (appIcon != null) {
            holder.appIconImageView.setImageDrawable(appIcon);
        } else {
            holder.appIconImageView.setImageResource(R.drawable.default_app_icon);
        }

        // Set category with color
        String category = appUsage.getCategory();
        holder.categoryTextView.setText(category);

        if ("Non-Productive".equals(category)) {
            holder.categoryTextView.setTextColor(Color.RED);
        } else {
            holder.categoryTextView.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return appUsageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView appNameTextView, usageTimeTextView, lastOpenedTextView, categoryTextView;
        ImageView appIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            usageTimeTextView = itemView.findViewById(R.id.usageTimeTextView);
            lastOpenedTextView = itemView.findViewById(R.id.lastOpenedTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
        }
    }

    private String formatUsageTime(long usageTime) {
        long seconds = usageTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
