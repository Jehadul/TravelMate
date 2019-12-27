package com.jihad.app.travelmate.models;

public class PrivateMessage extends BaseModel{

    private String uid;
    private String type;
    private String senderUid;
    private String message;

    public PrivateMessage() {
    }

    public PrivateMessage(String uid, String type, String senderUid, String message) {
        super();

        this.uid = uid;
        this.type = type;
        this.senderUid = senderUid;
        this.message = message;
    }

    public PrivateMessage(String uid, String type, String senderUid, String message, String createdDate, String createdTime) {
        super(createdDate, createdTime);

        this.uid = uid;
        this.type = type;
        this.senderUid = senderUid;
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public String getType() {
        return type;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getMessage() {
        return message;
    }
}
