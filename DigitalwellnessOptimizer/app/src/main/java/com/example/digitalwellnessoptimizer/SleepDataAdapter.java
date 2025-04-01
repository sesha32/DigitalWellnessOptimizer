package com.example.digitalwellnessoptimizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepDataAdapter extends RecyclerView.Adapter<SleepDataAdapter.SleepViewHolder> {

    private List<SleepDataModel> sleepDataList;

    public SleepDataAdapter(List<SleepDataModel> sleepDataList) {
        this.sleepDataList = sleepDataList;
    }

    @NonNull
    @Override
    public SleepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sleep_data, parent, false);
        return new SleepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepViewHolder holder, int position) {
        SleepDataModel sleepData = sleepDataList.get(position);

        // Convert timestamp (long) into a formatted date string
        String formattedTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(new Date(sleepData.getTimestamp()));

        holder.timeTextView.setText(formattedTime); // Display formatted time
        holder.statusTextView.setText(sleepData.getSleepStatus()); // Display sleep status
    }

    @Override
    public int getItemCount() {
        return sleepDataList.size();
    }

    public static class SleepViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, statusTextView;

        public SleepViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.sleep_time);
            statusTextView = itemView.findViewById(R.id.sleep_status);
        }
    }
}
