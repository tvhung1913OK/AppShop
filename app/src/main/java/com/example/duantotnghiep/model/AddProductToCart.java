package com.example.duantotnghiep.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AddProductToCart implements Parcelable {
    private String cartItemId;
    private String id_user;
    private String id_product;
    private String name_product;
    private Integer color_product;
    private String size_product;
    private String image_product;
    private int quantity_product;
    private double pricetotal_product;
    protected AddProductToCart(Parcel in) {
        cartItemId = in.readString();
        id_user = in.readString();
        id_product = in.readString();
        name_product = in.readString();
        if (in.readByte() == 0) {
            color_product = null;
        } else {
            color_product = in.readInt();
        }
        size_product = in.readString();
        image_product = in.readString();
        quantity_product = in.readInt();
        pricetotal_product = in.readDouble();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cartItemId);
        dest.writeString(id_user);
        dest.writeString(id_product);
        dest.writeString(name_product);
        if (color_product == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(color_product);
        }
        dest.writeString(size_product);
        dest.writeString(image_product);
        dest.writeInt(quantity_product);
        dest.writeDouble(pricetotal_product);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<AddProductToCart> CREATOR = new Creator<AddProductToCart>() {
        @Override
        public AddProductToCart createFromParcel(Parcel in) {
            return new AddProductToCart(in);
        }
        @Override
        public AddProductToCart[] newArray(int size) {
            return new AddProductToCart[size];
        }
    };
    public String getCartItemId() {
        return cartItemId;
    }
    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }
    public String getId_user() {
        return id_user;
    }
    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
    public String getId_product() {
        return id_product;
    }
    public void setId_product(String id_product) {
        this.id_product = id_product;
    }
    public String getName_product() {
        return name_product;
    }
    public void setName_product(String name_product) {
        this.name_product = name_product;
    }
    public Integer getColor_product() {
        return color_product;
    }
    public void setColor_product(Integer color_product) {
        this.color_product = color_product;
    }
    public String getSize_product() {
        return size_product;
    }
    public void setSize_product(String size_product) {
        this.size_product = size_product;
    }
    public String getImage_product() {
        return image_product;
    }
    public void setImage_product(String image_product) {
        this.image_product = image_product;
    }
    public int getQuantity_product() {
        return quantity_product;
    }
    public void setQuantity_product(int quantity_product) {
        this.quantity_product = quantity_product;
    }
    public double getPricetotal_product() {
        return pricetotal_product;
    }
    public void setPricetotal_product(double pricetotal_product) {
        this.pricetotal_product = pricetotal_product;
    }
    public AddProductToCart() {
    }
    public AddProductToCart(String cartItemId, String id_user, String id_product, String name_product, Integer color_product, String size_product, String image_product, int num_product, double pricetotal_product) {
        this.cartItemId = cartItemId;
        this.id_user = id_user;
        this.id_product = id_product;
        this.name_product = name_product;
        this.color_product = color_product;
        this.size_product = size_product;
        this.image_product = image_product;
        this.quantity_product = num_product;
        this.pricetotal_product = pricetotal_product;
    }
    public String getId() {
        return id_product;
    }
}