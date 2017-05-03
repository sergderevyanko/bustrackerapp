package com.infragps.gpstracker.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by sergey.derevyanko on 08.12.16.
 */

@JsonIgnoreProperties({ "updated_at", "created_at"})
public class Tracker {

    private int id;
    @JsonProperty("route_number")
    private String routeNumber;
    @JsonProperty("tracker_id")
    private int trackerId;

    private Date createdAt;

    private Date updatedAt;

    public Tracker() {
    }

    public Tracker(int id, String routeNumber) {
        this.id = id;
        this.routeNumber = routeNumber;
    }
    @ApiModelProperty
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @ApiModelProperty
    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }
    @ApiModelProperty
    public int getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(int trackerId) {
        this.trackerId = trackerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
