package com.airtraffic.demo.service;

import java.util.List;

public class ListResponse<T> {

    private List<T> items;

    private boolean hasMore;

    private ControlFilter filter;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public ControlFilter getFilter() {
        return filter;
    }

    public void setFilter(ControlFilter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "ListResponse{" +
                "items.size=" + (items == null ? "null" :  items.size()) +
                ", hasMore=" + hasMore +
                '}';
    }
}