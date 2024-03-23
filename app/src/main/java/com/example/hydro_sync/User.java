package com.example.hydro_sync;

// User.java
public class User {

    private String userName;
    private String houseNo;
    private String houseName;
    private String userId;
    private int level;
    private String request;

    public User() {
        // Required by Firebase
    }

    public User(String username, String houseNo, String houseName, Integer level) {
        this.userName = username;
        this.houseNo = houseNo;
        this.houseName = houseName;
        this.level = level;
        this.request = request;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
