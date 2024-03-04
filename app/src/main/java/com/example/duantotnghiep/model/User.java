package com.example.duantotnghiep.model;

import java.util.List;
import java.util.Map;

public class User {
    private String id,img,username,password,email,phone;
    private Boolean user_type,isLock;
    private Double wallet;
    List<Location> location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getUser_type() {
        return user_type;
    }

    public void setUser_type(Boolean user_type) {
        this.user_type = user_type;
    }

    public Boolean getLock() {
        return isLock;
    }

    public void setLock(Boolean lock) {
        isLock = lock;
    }

    public Double getWallet() {
        return wallet;
    }

    public void setWallet(Double wallet) {
        this.wallet = wallet;
    }

    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
    }

    public User() {
    }

    public User(String id, String img, String username, String password, String email, String phone, Boolean user_type, Boolean isLock, Double wallet, List<Location> location) {
        this.id = id;
        this.img = img;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.user_type = user_type;
        this.isLock = isLock;
        this.wallet = wallet;
        this.location = location;
    }
}
