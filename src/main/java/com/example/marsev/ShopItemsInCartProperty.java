package com.example.marsev;

public class ShopItemsInCartProperty {
    String hashCode;
    String name;
    String img;
    String price;
    String description;

    public ShopItemsInCartProperty() {
    }

    public ShopItemsInCartProperty(String hashCode, String name, String img, String price, String description) {
        this.hashCode = hashCode;
        this.name = name;
        this.img = img;
        this.price = price;
        this.description = description;
    }

    public String getHashCode() {
        return hashCode;
    }

    public String getName() {
        return name;
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
}
