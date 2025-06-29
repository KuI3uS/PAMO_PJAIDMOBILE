package com.example.pjaidmobile.data.repository;

import com.example.pjaidmobile.data.model.TicketRequest;
import com.example.pjaidmobile.data.model.TicketResponse;
import com.example.pjaidmobile.data.remote.api.TicketApi;
import com.example.pjaidmobile.domain.repository.TicketRepository;
import java.io.IOException;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Response;

public class TicketRepositoryImpl implements TicketRepository {
    private final TicketApi ticketApi;

    @Inject
    public TicketRepositoryImpl(TicketApi ticketApi) {
        this.ticketApi = ticketApi;
    }

    @Override
    public TicketResponse getTicketById(String id) {
        try {
            Response<TicketResponse> response = ticketApi.getTicketById(id).execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new RuntimeException("API error: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Network error", e);
        }
    }

    @Override
    public Call<TicketResponse> createTicket(TicketRequest request) {
        return ticketApi.createTicket(request);
    }
}
