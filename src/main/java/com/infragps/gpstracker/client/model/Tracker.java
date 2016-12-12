package com.infragps.gpstracker.client.model;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

/**
 * Created by sergey.derevyanko on 08.12.16.
 */

@ApiModel(description = "")
public class Tracker {

    private int id;
    @SerializedName("route_number")
    private String routeNumber;
    @SerializedName("tracker_id")
    private int trackerId;


    public Tracker(int id, String routeNumber) {
        this.id = id;
        this.routeNumber = routeNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public int getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(int trackerId) {
        this.trackerId = trackerId;
    }
}
