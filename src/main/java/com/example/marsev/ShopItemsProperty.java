package com.example.marsev;

public class ShopItemsProperty {
    String hashCode;
    String name;
    String img;
    String price;
    String description;

    public ShopItemsProperty() {
    }

    public ShopItemsProperty(String hashCode, String img, String price, String description, String name) {
        this.hashCode = hashCode;
        this.img = img;
        this.price = price;
        this.description = description;
        this.name = name;
    }

    public String getHashCode() {
        return hashCode;
    }

    public String getImg() {
        return img;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
    public String getItemName(){
        return  name;
    }
}
