package com.example.pjaidmobile.data.remote.api;

import com.example.pjaidmobile.data.model.IssueReport;
import com.example.pjaidmobile.data.model.PageResponse;
import com.example.pjaidmobile.data.model.ReportItem;
import com.example.pjaidmobile.data.model.TicketResponse;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReportApi {
    @POST("/reports")
    Completable sendReport(@Body IssueReport report);

    @GET("/ticket")
    Single<List<ReportItem>> getAllReports();
    @GET("/ticket/mine-paged")
    Single<PageResponse<TicketResponse>> getMyTicketsPaged(
            @Query("username") String username,
            @Query("page") int page,
            @Query("size") int size
    );
    @GET("/ticket/paged")
    Single<PageResponse<TicketResponse>> getAllTicketsPaged(
            @Query("page") int page,
            @Query("size") int size
    );



}
