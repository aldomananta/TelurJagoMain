package com.example.telurjago.Model;

public class Seller {

    private String name, password, phone, email, sid;

    public Seller(){



    }

    public Seller(String name, String password, String phone, String email, String sid) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
