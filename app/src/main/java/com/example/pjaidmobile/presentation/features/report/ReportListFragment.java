package com.example.pjaidmobile.presentation.features.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pjaidmobile.R;
import com.example.pjaidmobile.util.ButtonAnimationUtil;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReportListFragment extends Fragment {

    private ReportListViewModel viewModel;
    private ReportListAdapter adapter;

    private String username = "ZalogowanyUzytkownik";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewReports);
        TextView pageIndicator = view.findViewById(R.id.pageIndicator);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ReportListAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ReportListViewModel.class);
        viewModel.reports.observe(getViewLifecycleOwner(), adapter::submitList);
        viewModel.pageIndicator.observe(getViewLifecycleOwner(), pageIndicator::setText);

        viewModel.resetPagination();



    }
}
