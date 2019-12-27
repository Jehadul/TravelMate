package com.jihad.app.travelmate.models;

import java.io.Serializable;

public class Event extends BaseModel implements Serializable {

    private String uid;
    private String name;
    private String startLocation;
    private String destination;
    private double budget;
    private String startDate;
    private String endDate;
    private int startDateInt;
    private int endDateInt;

    public Event() {
    }

    public Event(String uid, String name, String startLocation, String destination, double budget, String startDate, String endDate, int startDateInt, int endDateInt) {
        super();

        this.uid = uid;
        this.name = name;
        this.startLocation = startLocation;
        this.destination = destination;
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startDateInt = startDateInt;
        this.endDateInt = endDateInt;
    }

    public Event(String uid, String name, String startLocation, String destination, double budget, String startDate, String endDate, int startDateInt, int endDateInt, String createdDate, String createdTime) {
        super(createdDate, createdTime);

        this.uid = uid;
        this.name = name;
        this.startLocation = startLocation;
        this.destination = destination;
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startDateInt = startDateInt;
        this.endDateInt = endDateInt;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getDestination() {
        return destination;
    }

    public double getBudget() {
        return budget;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getStartDateInt() {
        return startDateInt;
    }

    public int getEndDateInt() {
        return endDateInt;
    }
}
