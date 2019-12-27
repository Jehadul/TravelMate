package com.jihad.app.travelmate.models;

import com.jihad.app.travelmate.common.Functions;

public class BaseModel {

    private String createdDate;
    private String createdTime;

    BaseModel() {
        this.createdDate = Functions.currentDateFormat();
        this.createdTime = Functions.currentTimeFormat();
    }

    BaseModel(String createdDate, String createdTime) {
        this.createdDate = createdDate;
        this.createdTime = createdTime;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getCreatedTime() {
        return createdTime;
    }
}
