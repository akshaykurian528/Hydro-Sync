package com.example.hydro_sync;

public class UserRequests {
    private String userName,houseNo,houseName,mobileNo;

    public UserRequests() {
        // Default constructor required for calls to DataSnapshot.getValue(UserRequest.class)
    }

    public String getUsername() {
        return userName;
    }

    public void userName(String userName) {
        this.userName = userName;
    }

    public String gethouseNo() {
        return houseNo;
    }

    public void houseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String gethouseName() {
        return houseName;
    }

    public void houseName(String houseName) {
        this.houseName = houseName;
    }

    public String getmobileNo() {
        return mobileNo;
    }

    public void mobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
