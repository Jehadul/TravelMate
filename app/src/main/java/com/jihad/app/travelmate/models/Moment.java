package com.jihad.app.travelmate.models;

public class Moment extends BaseModel {

    private String uid;
    private String imgUrl;
    private String eventName;
    private String remark;

    public Moment() {
    }

    public Moment(String uid, String imgUrl, String eventName, String remark) {
        super();

        this.uid = uid;
        this.imgUrl = imgUrl;
        this.eventName = eventName;
        this.remark = remark;
    }

    public Moment(String uid, String imgUrl, String eventName, String remark, String createdDate, String createdTime) {
        super(createdDate, createdTime);

        this.uid = uid;
        this.imgUrl = imgUrl;
        this.eventName = eventName;
        this.remark = remark;
    }

    public String getUid() {
        return uid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getEventName() {
        return eventName;
    }

    public String getRemark() {
        return remark;
    }
}
