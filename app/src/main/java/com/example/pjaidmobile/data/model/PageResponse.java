package com.example.pjaidmobile.data.model;

import java.util.List;

public class PageResponse<T> {
    public List<T> content;
    public int totalPages;
    public int totalElements;
    public int number;
    public boolean last;

}

