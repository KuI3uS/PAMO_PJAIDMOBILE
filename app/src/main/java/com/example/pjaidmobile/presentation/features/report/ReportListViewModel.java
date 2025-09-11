package com.example.pjaidmobile.presentation.features.report;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.pjaidmobile.data.model.ReportItem;
import com.example.pjaidmobile.data.model.TicketResponse;
import com.example.pjaidmobile.domain.repository.ReportRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class ReportListViewModel extends ViewModel {

    private final ReportRepository repository;
    private final MutableLiveData<List<ReportItem>> _reports = new MutableLiveData<>();
    public LiveData<List<ReportItem>> reports = _reports;
    private final MutableLiveData<String> _pageIndicator = new MutableLiveData<>("Strona 1 z 1");
    public LiveData<String> pageIndicator = _pageIndicator;

    private Disposable disposable;

    private int currentPage = 0;
    private final int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private boolean showingMine = false;

    @Inject
    public ReportListViewModel(ReportRepository repository) {
        this.repository = repository;
         //fetchReports();
        loadAllTicketsPage(0);
    }
    public void setShowingMine(boolean mine) {
        this.showingMine = mine;
    }


//    private void fetchReports() {
//        disposable = repository.getAllReports()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        _reports::setValue,
//                        throwable -> Log.e("ReportViewModel", "Błąd pobierania raportów", throwable)
//                );
//    }

    public void loadPage(String username, int page) {
        if (isLoading) return;

        isLoading = true;

        disposable = repository.getMyTicketsPaged(username, page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            List<ReportItem> currentList = new ArrayList<>();

                            for (TicketResponse ticket : response.content) {
                                currentList.add(new ReportItem(
                                        String.valueOf(ticket.getId()),
                                        ticket.getDescription(),
                                        ticket.getCreatedAt(),
                                        ticket.getTitle(),
                                        ticket.getTechnicianName(),
                                        ticket.getStatus()
                                ));
                            }

                            _reports.setValue(currentList);

                            currentPage = response.number; // numer strony z backendu
                            isLastPage = response.last;

                            _pageIndicator.setValue("Strona " + (currentPage + 1) + " z " + response.totalPages);
                            isLoading = false;
                        },
                        throwable -> {
                            Log.e("ReportViewModel", "Błąd ładowania stron ticketów", throwable);
                            isLoading = false;
                        }
                );
    }

    public void loadAllTicketsPage(int page) {
        if (isLoading) return;

        isLoading = true;

        disposable = repository.getAllTicketsPaged(page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            List<ReportItem> currentList = new ArrayList<>();

                            for (TicketResponse ticket : response.content) {
                                currentList.add(new ReportItem(
                                        String.valueOf(ticket.getId()),
                                        ticket.getDescription(),
                                        ticket.getCreatedAt(),
                                        ticket.getTitle(),
                                        ticket.getTechnicianName(),
                                        ticket.getStatus()
                                ));
                            }

                            _reports.setValue(currentList);

                            currentPage = response.number;
                            isLastPage = response.last;
                            _pageIndicator.setValue("Strona " + (currentPage + 1) + " z " + response.totalPages);

                            isLoading = false;
                        },
                        throwable -> {
                            Log.e("ReportViewModel", "Błąd ładowania stron ticketów", throwable);
                            isLoading = false;
                        }
                );
    }


    public void nextPage(String username) {
        if (!isLastPage && !isLoading) {
            if (showingMine) {
                loadPage(username, currentPage + 1);
            } else {
                loadAllTicketsPage(currentPage + 1);
            }
        }
    }

    public void previousPage(String username) {
        if (currentPage > 0 && !isLoading) {
            if (showingMine) {
                loadPage(username, currentPage - 1);
            } else {
                loadAllTicketsPage(currentPage - 1);
            }
        }
    }


    public void resetPagination() {
        currentPage = 0;
        isLastPage = false;
        _reports.setValue(new ArrayList<>());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}