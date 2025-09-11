package com.example.pjaidmobile.presentation.features.report;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pjaidmobile.R;
import com.example.pjaidmobile.data.model.ReportItem;
import com.example.pjaidmobile.util.ButtonAnimationUtil;
import com.google.android.material.tabs.TabLayout;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReportListActivity extends AppCompatActivity {

    private ReportListViewModel viewModel;
    private ReportListAdapter adapter;

    private String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        SharedPreferences prefs = getSharedPreferences("PJAIDPrefs", MODE_PRIVATE);
        loggedInUsername = prefs.getString("loggedInUsername", null);


        if (loggedInUsername == null) {
            Toast.makeText(this, "Nie znaleziono danych użytkownika. Zaloguj się ponownie.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
        Button buttonPrevious = findViewById(R.id.buttonPrevious);
        Button buttonNext = findViewById(R.id.buttonNext);
        TextView pageIndicator = findViewById(R.id.pageIndicator);

        ButtonAnimationUtil.applySpringAnimation(buttonPrevious);
        ButtonAnimationUtil.applySpringAnimation(buttonNext);

        buttonPrevious.setOnClickListener(v -> viewModel.previousPage(loggedInUsername));
        buttonNext.setOnClickListener(v -> viewModel.nextPage(loggedInUsername));


        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Wszystkie"));
        tabLayout.addTab(tabLayout.newTab().setText("Moje"));


        RecyclerView recyclerView = findViewById(R.id.recyclerViewReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportListAdapter();
        recyclerView.setAdapter(adapter);


        viewModel = new ViewModelProvider(this).get(ReportListViewModel.class);
        viewModel.reports.observe(this, reports -> {
            Log.d("ReportListActivity", "Dostałem dane z API: " + reports);
            adapter.submitList(reports);
        });

        viewModel.pageIndicator.observe(this, pageIndicator::setText);

        viewModel.resetPagination();
        viewModel.loadPage(loggedInUsername, 0);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Wszystkie
                    viewModel.resetPagination();
                    viewModel.setShowingMine(false);
                    viewModel.loadAllTicketsPage(0);
                } else {
                    // Moje
                    viewModel.resetPagination();
                    viewModel.setShowingMine(true);
                    viewModel.loadPage(loggedInUsername, 0);
                }


            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

    }
}
