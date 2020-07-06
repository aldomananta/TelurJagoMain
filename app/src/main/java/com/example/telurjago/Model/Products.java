package com.example.telurjago.Model;

public class Products {

    private String category, description, price, image, pid, date, time, trays, sid, name;

    public Products(){


    }

    public Products(String category, String description, String price, String image, String pid, String date, String time, String trays, String sid, String name) {
        this.category = category;
        this.description = description;
        this.price = price;
        this.image = image;
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.trays = trays;
        this.sid = sid;
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrays() {
        return trays;
    }

    public void setTrays(String trays) {
        this.trays = trays;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
