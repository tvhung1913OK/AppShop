package com.example.duantotnghiep.model;

import java.util.List;

public class Order {
    private String id,idBuyer,idSeller;
    private Double total;
    private String status;
    private String address;
    private String numberPhone;
    private Boolean paid;
    private String note;
    private String date;
    private List<InfoProductOrder> listProduct;
    private String customerName;
    private String customerImage;
    private int totalQuantity;
    public Order(String id, String idBuyer, String idSeller, Double total, String address, String numberPhone, boolean paid, String status, String note, String date, List<InfoProductOrder> listProduct, String customerName, String customerImage) {
        this.id = id;
        this.idBuyer = idBuyer;
        this.idSeller = idSeller;
        this.total = total;
        this.address = address;
        this.numberPhone = numberPhone;
        this.paid = Boolean.valueOf(paid);
        this.status = String.valueOf(status);
        this.note = note;
        this.date = date;
        this.listProduct = listProduct;
        this.customerName = customerName;
        this.customerImage = customerImage;
        this.totalQuantity = totalQuantity;
    }
    public Order() {
    }
    public String getDate() {
        return date;
    }
    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerImage() {
        return customerImage;
    }
    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }
    public String getStatus() {
        return status;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIdBuyer() {
        return idBuyer;
    }
    public void setIdBuyer(String idBuyer) {
        this.idBuyer = idBuyer;
    }
    public String getIdSeller() {
        return idSeller;
    }
    public void setIdSeller(String idSeller) {
        this.idSeller = idSeller;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getNumberPhone() {
        return numberPhone;
    }
    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }
    public Boolean getPaid() {
        return paid;
    }
    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
    public List<InfoProductOrder> getListProduct() {
        return listProduct;
    }
    public void setListProduct(List<InfoProductOrder> listProduct) {
        this.listProduct = listProduct;
    }
}