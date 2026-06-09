package com.tijil.reminder;

public class Reminder {
    private int id;
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private int radius;
    private int isActive;

    public Reminder(int id, String title, String description, double latitude, double longitude, String address, int radius, int isActive) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.radius = radius;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public int getRadius() {
        return radius;
    }

    public int getIsActive() {
        return isActive;
    }
}