package com.example.pjaidmobile.data.remote.api;

import com.example.pjaidmobile.data.model.PageResponse;
import com.example.pjaidmobile.data.model.TicketResponse;
import com.example.pjaidmobile.data.model.TicketRequest;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;


public interface TicketApi {
    @GET("ticket")
    Call<List<TicketResponse>> getAllTickets();

    @GET("ticket/{id}")
    Call<TicketResponse> getTicket(@Path("id") int id);

    @POST("ticket")
    Call<TicketResponse> createTicket(@Body TicketRequest ticketRequest);

    @GET("ticket/{id}")
    Call<TicketResponse> getTicketById(@Path("id") String id);

    @GET("ticket/mine-paged")
    Call<PageResponse<TicketResponse>> getMyTicketsPaged(
            @Query("username") String username,
            @Query("page") int page,
            @Query("size") int size
    );
    @PATCH("ticket/{id}/status")
    Call<TicketResponse> updateTicketStatus(
            @Path("id") Long ticketId,
            @Body Map<String, String> statusUpdate
    );
    @GET("technicians")
    Call<List<String>> getTechnicians();

    @PATCH("ticket/{id}/assignee")
    Call<TicketResponse> updateTicketAssignee(
            @Path("id") Long id,
            @Body Map<String, String> assigneeUpdate
    );
    @Streaming
    @GET("ticket/{id}/report")
    Call<ResponseBody> downloadTicketReport(@Path("id") Long ticketId);
}