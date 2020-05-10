package com.example.marsev;

public class SalesProperty {
    String saleName, saleItems, saleTotal, saleNumber;


    public SalesProperty() {
    }

    public SalesProperty(String saleName, String saleItems, String saleTotal, String saleNumber) {
        this.saleName = saleName;
        this.saleItems = saleItems;
        this.saleTotal = saleTotal;
        this.saleNumber = saleNumber;
    }

    public String getsaleName() {
        return saleName;
    }

    public String getsaleItems() {
        return saleItems.replace(',','\n');
    }

    public String getsaleTotal() {
        return saleTotal;
    }

    public String getsaleNumber() {
        return saleNumber;
    }
}
