package com.example.marsev;

public class NotificationInfoProperty {
    private String name;
    private String description;

    public NotificationInfoProperty() {
    }

    public NotificationInfoProperty(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
