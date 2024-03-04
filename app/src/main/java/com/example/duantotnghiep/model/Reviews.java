package com.example.duantotnghiep.model;

public class Reviews {
    private String id;
    private String userId;
    private String displayName;
    private String imgUser;
    private String idOrder;
    private String productId;
    private int start;
    private String comment;
    private String time;
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public Reviews() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getImgUser() {
        return imgUser;
    }
    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }
    public String getIdOrder() {
        return idOrder;
    }
    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public Reviews(String id, String userId, String displayName, String imgUser, String idOrder, String productId, int start, String comment, String time) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.imgUser = imgUser;
        this.idOrder = idOrder;
        this.productId = productId;
        this.start = start;
        this.comment = comment;
        this.time = time;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}