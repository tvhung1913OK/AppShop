package com.example.duantotnghiep.model;


import java.util.ArrayList;

public class ColorProduct {
    private int color = 0;
    private ArrayList<Integer> quantity;
    public ColorProduct() {

    }
    public ColorProduct(int color) {
        this.color = color;
    }

    public ColorProduct(int color, int[] quantity) {
        this.color = color;
        this.quantity = new ArrayList<>();
        for(int i = 0; i < quantity.length; i++) {
            this.quantity.add(quantity[i]);
        }
    }

    @Override
    public String toString() {
        String ret = "ColorProduct{" +
                "color=" + color + ", quantity={";
        for(int i : quantity) {
            ret += ", " + i;
        }
        ret += "}}";
        return ret;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<Integer> getQuantity() {
        return quantity;
    }

    public void setQuantity(ArrayList<Integer> quantity) {
        this.quantity = quantity;
    }
}
