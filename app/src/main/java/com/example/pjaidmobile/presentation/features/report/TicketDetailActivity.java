package com.example.pjaidmobile.presentation.features.report;

import android.annotation.SuppressLint;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pjaidmobile.R;
import com.example.pjaidmobile.data.model.TicketResponse;
import com.example.pjaidmobile.data.remote.api.ApiClient;
import com.example.pjaidmobile.data.remote.api.TicketApi;
import com.example.pjaidmobile.util.ButtonAnimationUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity that displays the details of the request (ticket).
 * Retrieves ticket details from the backend based on the submitted ID
 * and enables editing.
 */
public class TicketDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvDevice;
    private TextView tvStatus;
    private TextView tvAssignee;
    private TextView tvDate;
    private TicketResponse ticket;
    private Spinner spinnerStatus;
    private Spinner spinnerAssignee;

    private static final String TAG_TICKET = "TICKET";
    private static final String ERROR_CONNECTION_PREFIX = "Błąd połączenia: ";

    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayout formContainer;
    private boolean isFormVisible = false;

    private TicketApi ticketApi;

    private ArrayAdapter<CharSequence> statusAdapter;
    private ArrayAdapter<CharSequence> techAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        ticketApi = ApiClient.getClient().create(TicketApi.class);

        initViews();
        setupSpinners();
        attachHandlers();
        handleIntentAndLoad();
    }


    private void initViews() {
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tv_detail_title);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvDevice = findViewById(R.id.tv_detail_device);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvAssignee = findViewById(R.id.tv_detail_assignee);
        tvDate = findViewById(R.id.tv_detail_date);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerAssignee = findViewById(R.id.spinner_assignee);
        formContainer = findViewById(R.id.form_container);

        Button btnEdit = findViewById(R.id.btn_edit_ticket);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnExportCsv = findViewById(R.id.btn_export_csv);

        btnSave.setVisibility(View.GONE);
        btnExportCsv.setVisibility(View.GONE);

        ButtonAnimationUtil.applySpringAnimation(btnEdit);
        ButtonAnimationUtil.applySpringAnimation(btnSave);
        ButtonAnimationUtil.applySpringAnimation(btnExportCsv);
    }

    private void setupSpinners() {
        statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.edit_status_options, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        techAdapter = ArrayAdapter.createFromResource(
                this, R.array.technicians_list, android.R.layout.simple_spinner_item);
        techAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignee.setAdapter(techAdapter);
    }

    private void attachHandlers() {
        Button btnEdit = findViewById(R.id.btn_edit_ticket);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnExportCsv = findViewById(R.id.btn_export_csv);

        btnEdit.setOnClickListener(v -> {
            toggleEditMode(true);
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            btnExportCsv.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> onSaveClicked(btnSave, btnEdit, btnExportCsv));

        btnExportCsv.setOnClickListener(v -> {
            if (ticket == null) return;
            ticketApi.downloadTicketReport(ticket.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call,
                                       @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // try-with-resources dla ResponseBody
                        try (ResponseBody body = response.body()) {
                            saveCsvToDownloads(body, "ticket_" + ticket.getId() + ".csv");
                        } catch (IOException e) {
                            Toast.makeText(TicketDetailActivity.this,
                                    "Błąd zapisu pliku", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TicketDetailActivity.this,
                                "Błąd pobierania raportu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(TicketDetailActivity.this,
                            ERROR_CONNECTION_PREFIX + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @SuppressLint("SetTextI18n")
    private void handleIntentAndLoad() {
        String reportIdStr = getIntent().getStringExtra("reportId");
        if (reportIdStr == null) {
            Toast.makeText(this, "No notification ID", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int reportIdInt = Integer.parseInt(reportIdStr);
            tvTitle.setText("Zgłoszenie #" + reportIdStr);
            loadTicketFromBackend(reportIdInt);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Incorrect application ID", Toast.LENGTH_SHORT).show();
        }
    }


    private void toggleEditMode(boolean show) {
        formContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        tvStatus.setVisibility(show ? View.GONE : View.VISIBLE);
        spinnerStatus.setVisibility(show ? View.VISIBLE : View.GONE);
        tvAssignee.setVisibility(show ? View.GONE : View.VISIBLE);
        spinnerAssignee.setVisibility(show ? View.VISIBLE : View.GONE);
        isFormVisible = show;
    }

    private void onSaveClicked(Button btnSave, Button btnEdit, Button btnExportCsv) {
        if (!isFormVisible) return;

        toggleEditMode(false);

        EditText etActionDescription = findViewById(R.id.et_action_description);
        EditText etDuration = findViewById(R.id.et_duration);
        EditText etFinalNote = findViewById(R.id.et_final_note);

        if (ticket == null) {
            Toast.makeText(this, "Nie można zapisać – brak danych zgłoszenia", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            saveFormDataToFile(
                    ticket.getId(),
                    etActionDescription.getText().toString(),
                    etDuration.getText().toString(),
                    etFinalNote.getText().toString()
            );
        } catch (IOException e) {
            Toast.makeText(this, "Błąd zapisu pliku", Toast.LENGTH_SHORT).show();
        }

        btnSave.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        btnExportCsv.setVisibility(View.VISIBLE);

        updateStatus(spinnerStatus.getSelectedItem().toString());
        updateAssignee(spinnerAssignee.getSelectedItem().toString());
    }

    private void saveFormDataToFile(long ticketId, String opis, String czas, String uwagi) throws IOException {
        String dataToSave = "Opis działań: " + opis + "\n"
                + "Czas trwania: " + czas + " minut\n"
                + "Uwagi końcowe: " + uwagi;

        File dir = new File(getExternalFilesDir(null), "ticket_form_data");
        boolean ok = dir.exists() || dir.mkdirs();
        if (!ok) {
            throw new IOException("Nie można utworzyć katalogu: " + dir.getAbsolutePath());
        }

        File file = new File(dir, "ticket_form_" + ticketId + ".txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(dataToSave.getBytes(StandardCharsets.UTF_8));
        }
        Toast.makeText(this, "Zapisano dane do: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }

    @SuppressLint("SetTextI18n")
    private void updateStatus(String selectedStatus) {
        String backendStatus = mapStatusToBackend(selectedStatus);
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", backendStatus);

        ticketApi.updateTicketStatus(ticket.getId(), statusUpdate).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(@NonNull Call<TicketResponse> call,
                                   @NonNull Response<TicketResponse> response) {
                if (response.isSuccessful()) {
                    tvStatus.setText("Status: " + selectedStatus);
                    spinnerStatus.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                    Toast.makeText(TicketDetailActivity.this,
                            "Zmieniono status na: " + selectedStatus, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TicketDetailActivity.this,
                            "Nie udało się zmienić statusu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TicketResponse> call, @NonNull Throwable t) {
                Toast.makeText(TicketDetailActivity.this,
                        ERROR_CONNECTION_PREFIX + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateAssignee(String selectedTechnician) {
        if ("-- wybierz technika --".equals(selectedTechnician)) return;

        Map<String, String> technicianUpdate = new HashMap<>();
        technicianUpdate.put("assignee", selectedTechnician);

        ticketApi.updateTicketAssignee(ticket.getId(), technicianUpdate).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(@NonNull Call<TicketResponse> call,
                                   @NonNull Response<TicketResponse> response) {
                if (response.isSuccessful()) {
                    tvAssignee.setText("Przypisany do: " + selectedTechnician);
                    spinnerAssignee.setVisibility(View.GONE);
                    tvAssignee.setVisibility(View.VISIBLE);
                    Toast.makeText(TicketDetailActivity.this,
                            "Przypisano do: " + selectedTechnician, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TicketDetailActivity.this,
                            "Nie udało się przypisać technika", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TicketResponse> call, @NonNull Throwable t) {
                Toast.makeText(TicketDetailActivity.this,
                        ERROR_CONNECTION_PREFIX + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void loadTicketFromBackend(int id) {
        ticketApi.getTicket(id).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(@NonNull Call<TicketResponse> call,
                                   @NonNull Response<TicketResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showShort("Failed to download the application");
                    return;
                }
                handleTicket(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<TicketResponse> call, @NonNull Throwable t) {
                showShort("Connection error: " + t.getMessage());
            }
        });
    }

    private void handleTicket(@NonNull TicketResponse t) {
        ticket = t;
        bindTicketBasics(t);
        logTicket(t);
        setStatusSelection(mapDisplayToUi(t.getStatus()));
        setAssigneeSelection(t.getTechnicianName());
    }

    @SuppressLint("SetTextI18n")
    private void bindTicketBasics(@NonNull TicketResponse t) {
        tvTitle.setText(t.getTitle());
        tvDescription.setText("Opis zgłoszenia: " + t.getDescription());
        String deviceName = (t.getDevice() != null && t.getDevice().getName() != null)
                ? t.getDevice().getName()
                : "Nieprzypisane";
        tvDevice.setText("Urządzenie: " + deviceName);
        tvStatus.setText("Status: " + t.getStatus());
        tvAssignee.setText("Przypisany do: " + t.getTechnicianName());
        tvDate.setText("Utworzono: " + t.getCreatedAt());
    }

    private void logTicket(@NonNull TicketResponse t) {
        Log.d(TAG_TICKET, "Device: " + t.getDevice().getName());
        Log.d(TAG_TICKET, "User: " + t.getUser().getUserName());
        if (t.getIncident() != null) {
            Log.d(TAG_TICKET, "Incydent: " + t.getIncident().getTitle());
        } else {
            Log.w(TAG_TICKET, "No related incident in this report");
        }
    }

    private String mapDisplayToUi(@NonNull String backendStatus) {
        switch (backendStatus) {
            case "W_TRAKCIE":  return "W TRAKCIE";
            case "ZAMKNIETE":  return "ZAMKNIĘTE";
            default:           return backendStatus; // NOWE lub inne
        }
    }

    private void setStatusSelection(@NonNull String displayStatus) {
        int pos = statusAdapter.getPosition(displayStatus);
        if (pos >= 0) spinnerStatus.setSelection(pos);
    }

    private void setAssigneeSelection(String technicianName) {
        if (technicianName == null) return;
        int posTech = techAdapter.getPosition(technicianName);
        if (posTech >= 0) spinnerAssignee.setSelection(posTech);
    }

    private void showShort(@NonNull String msg) {
        Toast.makeText(TicketDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    private String mapStatusToBackend(String displayStatus) {
        switch (displayStatus) {
            case "NOWE":
                return "NOWE";
            case "W TRAKCIE":
                return "W_TRAKCIE";
            case "ZAMKNIĘTE":
                return "ZAMKNIETE";
            default:
                return displayStatus;
        }
    }


    private void saveCsvToDownloads(ResponseBody body, String fileName) throws IOException {
        File downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir != null && !downloadsDir.exists()) {
            boolean ok = downloadsDir.mkdirs();                // <- nie ignorujemy
            if (!ok) throw new IOException("Nie można utworzyć katalogu: " + downloadsDir.getAbsolutePath());
        }

        File file = new File(downloadsDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(body.bytes());
        }

        MediaScannerConnection.scanFile(this,
                new String[]{file.getAbsolutePath()}, null, null);

        Toast.makeText(this, "Plik zapisano: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }
}
