package com.example.pjaidmobile.presentation.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pjaidmobile.R;
import com.example.pjaidmobile.presentation.features.auth.LoginActivity;
import com.example.pjaidmobile.presentation.features.report.CreateTicketActivity;
import com.example.pjaidmobile.presentation.features.report.ReportListActivity;
import com.example.pjaidmobile.presentation.features.scan.ScanQRActivity;
import com.example.pjaidmobile.util.ButtonAnimationUtil;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("PJAIDPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        Button scanQR = findViewById(R.id.buttonScanQR);
        Button reportIssue = findViewById(R.id.buttonReportIssue);
        Button reportList = findViewById(R.id.buttonReportList);
        Button logoutButton = findViewById(R.id.logout_button);

        TextView loggedUserText = findViewById(R.id.tv_logged_user);
        String loggedUser = prefs.getString("username", getString(R.string.unknown_user));
        loggedUserText.setText(getString(R.string.logged_user, loggedUser));



        ButtonAnimationUtil.applySpringAnimation(scanQR);
        ButtonAnimationUtil.applySpringAnimation(reportIssue);
        ButtonAnimationUtil.applySpringAnimation(reportList);
        ButtonAnimationUtil.applySpringAnimation(logoutButton);

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        scanQR.setOnClickListener(v ->
                startActivity(new Intent(this, ScanQRActivity.class)));

        reportIssue.setOnClickListener(v ->
                startActivity(new Intent(this, CreateTicketActivity.class)));

        reportList.setOnClickListener(v ->
                startActivity(new Intent(this, ReportListActivity.class)));


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);

        }
    }
}