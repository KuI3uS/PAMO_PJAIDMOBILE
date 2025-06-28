package com.example.pjaidmobile.presentation.features.report;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pjaidmobile.R;
import com.example.pjaidmobile.data.model.TicketResponse;
import com.example.pjaidmobile.data.remote.api.ApiClient;
import com.example.pjaidmobile.data.remote.api.TicketApi;
import com.example.pjaidmobile.util.ButtonAnimationUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity that displays the details of the request (ticket).
 * Retrieves ticket details from the backend based on the submitted ID.
 * and enables its editing.
 */

public class TicketDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvStatus;
    private TextView tvAssignee;
    private TextView tvDate;
    private TicketResponse ticket;

    private static final String TAG_TICKET = "TICKET";

    private TicketApi ticketApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tv_detail_title);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvAssignee = findViewById(R.id.tv_detail_assignee);
        tvDate = findViewById(R.id.tv_detail_date);
        View btnEdit = findViewById(R.id.btn_edit_ticket);
        View btnSave = findViewById(R.id.btn_save);

        ButtonAnimationUtil.applySpringAnimation(btnEdit);
        ButtonAnimationUtil.applySpringAnimation(btnSave);

        ticketApi = ApiClient.getClient().create(TicketApi.class);

        String reportIdStr = getIntent().getStringExtra("reportId");
        if (reportIdStr != null) {
            try {
                int reportIdInt = Integer.parseInt(reportIdStr);
                tvTitle.setText("Zgłoszenie #" + reportIdStr);
                loadTicketFromBackend(reportIdInt);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Incorrect application ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No notification ID", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadTicketFromBackend(int id) {
        ticketApi.getTicket(id).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ticket = response.body();
                    tvTitle.setText(ticket.getTitle());
                    tvDescription.setText("Opis zgłoszenia: " + ticket.getDescription());
                    tvStatus.setText("Status: " + ticket.getStatus());
                    tvAssignee.setText("Przypisany do: " + ticket.getTechnicianName());
                    tvDate.setText("Utworzono: " + ticket.getCreatedAt());
                    Log.d(TAG_TICKET, "Device: " + ticket.getDevice().getName());
                    Log.d(TAG_TICKET, "User: " + ticket.getUser().getUserName());
                    if (ticket.getIncident() != null) {
                        Log.d(TAG_TICKET, "Incydent: " + ticket.getIncident().getTitle());
                    } else {
                        Log.w(TAG_TICKET, "No related incident in this report");
                    }
                } else {
                    Toast.makeText(TicketDetailActivity.this, "Failed to download the application", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                Toast.makeText(TicketDetailActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}



