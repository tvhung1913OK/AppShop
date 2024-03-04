package com.example.duantotnghiep.model;

public class Statistical {
    private String idProduct;
    private String imgProduct;
    private String nameProduct;
    private int totalQuantity;
    private double totalAmount;

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getImgProduct() {
        return imgProduct;
    }

    public void setImgProduct(String imgProduct) {
        this.imgProduct = imgProduct;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public Statistical(String idProduct, String imgProduct, String nameProduct, int totalQuantity, double totalAmount) {
        this.idProduct = idProduct;
        this.imgProduct = imgProduct;
        this.nameProduct = nameProduct;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }

    public Statistical() {
    }
}
