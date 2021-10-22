package com.airtraffic.demo.service;

import org.bson.types.ObjectId;

public class ControlFilter {

    /**
     * Search string
     */
    private String search;

    /**
     * Pagination property - last loaded object ID
     */
    private ObjectId lastId;

    /**
     * Page size
     */
    private int limit;

    private ControlAircraft.Type type;
    private ControlAircraft.Size size;

    public ControlAircraft.Type getType() {
        return type;
    }

    public void setType(ControlAircraft.Type type) {
        this.type = type;
    }

    public ControlAircraft.Size getSize() {
        return size;
    }

    public void setSize(ControlAircraft.Size size) {
        this.size = size;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public ObjectId getLastId() {
        return lastId;
    }

    public void setLastId(ObjectId lastId) {
        this.lastId = lastId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "ControlFilter{" +
                "search='" + search + '\'' +
                ", lastId=" + lastId +
                ", limit=" + limit +
                ", type=" + type +
                ", size=" + size +
                '}';
    }
}