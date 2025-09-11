package com.example.pjaidmobile.domain.repository;

import com.example.pjaidmobile.data.model.IssueReport;
import com.example.pjaidmobile.data.model.PageResponse;
import com.example.pjaidmobile.data.model.ReportItem;
import com.example.pjaidmobile.data.model.TicketResponse;

import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface ReportRepository {
    Completable sendReport(IssueReport report);

    Single<List<ReportItem>> getAllReports();
    Single<ReportItem> getReportById(String id);
    Single<PageResponse<TicketResponse>> getMyTicketsPaged(String username, int page, int size);
    Single<PageResponse<TicketResponse>> getAllTicketsPaged(int page, int size);


}
