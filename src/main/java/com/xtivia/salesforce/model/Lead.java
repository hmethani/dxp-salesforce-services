package com.xtivia.salesforce.model;

public class Lead {

    private final String id;

    private final String company;
    private final String status;

    private final String name;
    private String       title;
    private final String email;
    private String       phone;
    private String       mobilePhone;

    private String       city;
    private String       state;
    private String       country;

    private String       description;
    private String       website;
    private String       numberOfEmployees;

    public Lead(String id, String company, String name, String status, String email, String city) {
        this.id = id;
        this.company = company;
        this.status = status;

        this.name = name;
        this.email = email;
        this.city = city;

        title = phone = mobilePhone = state = country = description = website = numberOfEmployees = "";
    }

    public String getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getTitle() {
        return title;
    }

    public String getPhone() {
        return phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public String getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setNumberOfEmployees(String numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

}
