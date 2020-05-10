package com.example.marsev;

public class ShopsProperty {
    String location;
    String sName;
    String imgLogo;
    String sDescription;
    String owner;
    String phone;

    public ShopsProperty(String location, String sName, String imgLogo, String sDescription, String phone, String owner) {
        this.location = location;
        this.sName = sName;
        this.imgLogo = imgLogo;
        this.sDescription = sDescription;
        this.owner = owner;
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return sName;
    }

    public String getImgLogo() {
        return imgLogo;
    }

    public String getDescription() {
        return sDescription;
    }

    public  String getOwner(){
        return owner;
    }

    public String getPhone(){
        return phone;
    }
}
