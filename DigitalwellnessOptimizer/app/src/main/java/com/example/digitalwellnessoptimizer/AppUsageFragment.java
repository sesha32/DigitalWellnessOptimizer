package com.example.digitalwellnessoptimizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppUsageFragment extends Fragment {
    private RecyclerView recyclerView;
    private AppUsageAdapter adapter;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_usage, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        dbHelper = new DatabaseHelper(requireContext());
        List<AppUsageModel> appUsageList = dbHelper.getAllAppUsage();

        if (!appUsageList.isEmpty()) {
            adapter = new AppUsageAdapter(requireContext(), appUsageList);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
