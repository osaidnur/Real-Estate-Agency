package com.example.a1210733_1211088_courseproject.models;

public class Property {
    private long propertyId;
    private String type; // "Apartment", "villa", "land"
    private String title ;
    private String description;
    private int bedrooms;
    private int bathrooms;
    private float area; // in square meters
    private double price;
    private String country ;
    private String city ;
    private String imageUrl; // URL to the property image
    private boolean isSpecial; // Flag for special properties
    private double discount; // Discount percentage

    // Full constructor
    public Property(long propertyId, String type, String title,
                    String description, int bedrooms, int bathrooms,
                    float area, double price, String country, String city,
                    String imageUrl, boolean isSpecial, double discount) {
        this.propertyId = propertyId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.area = area;
        this.price = price;
        this.country = country;
        this.city = city;
        this.imageUrl = imageUrl;
        this.isSpecial = isSpecial;
        this.discount = discount;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // Calculate the discounted price if applicable
    public double getDiscountedPrice() {
        if (isSpecial && discount > 0) {
            return price * (1 - (discount / 100.0));
        }
        return price;
    }

    @Override
    public String toString() {
        return "Property{" +
                "propertyId=" + propertyId +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", area=" + area +
                ", price=" + price +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isSpecial=" + isSpecial +
                ", discount=" + discount +
                '}';
    }
}
