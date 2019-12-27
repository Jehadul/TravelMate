package com.jihad.app.travelmate.models;

import java.io.Serializable;

public class User extends BaseModel implements Serializable {

    private String uid;
    private String name;
    private String gender;
    private String email;
    private String phone;
    private String status;
    private String imgUrl;

    public User() {
    }

    public User(String uid, String name, String gender, String email, String phone, String status, String imgUrl) {
        super();

        this.uid = uid;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.imgUrl = imgUrl;
    }

    public User(String uid, String name, String gender, String email, String phone, String status, String imgUrl, String createdDate, String createdTime) {
        super(createdDate, createdTime);

        this.uid = uid;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.imgUrl = imgUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

}
