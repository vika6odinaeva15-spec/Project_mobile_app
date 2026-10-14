package com.socialnavigator.models;

public class Discount {

    private int id;
    private String city;
    private String category;
    private String name;
    private String type;
    private String promotional;
    private String timeOfApplication;
    private String address;
    private String linkToWebsite;
    private double latitude;
    private double longitude;

    // Пустой конструктор — для Gson (Этап 5)
    public Discount() {
    }

    // Полный конструктор — для тестовых данных (Этап 2)
    public Discount(int id, String city, String category, String name,
                    String type, String promotional, String timeOfApplication,
                    String address, String linkToWebsite,
                    double latitude, double longitude) {
        this.id = id;
        this.city = city;
        this.category = category;
        this.name = name;
        this.type = type;
        this.promotional = promotional;
        this.timeOfApplication = timeOfApplication;
        this.address = address;
        this.linkToWebsite = linkToWebsite;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // --- Геттеры и сеттеры ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPromotional() {
        return promotional;
    }

    public void setPromotional(String promotional) {
        this.promotional = promotional;
    }

    public String getTimeOfApplication() {
        return timeOfApplication;
    }

    public void setTimeOfApplication(String timeOfApplication) {
        this.timeOfApplication = timeOfApplication;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLinkToWebsite() {
        return linkToWebsite;
    }

    public void setLinkToWebsite(String linkToWebsite) {
        this.linkToWebsite = linkToWebsite;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}