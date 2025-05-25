package com.example.a1210733_1211088_courseproject.models;

public class User {
    private long userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private String phone;
    private String country;
    private String city;
    private String role;  // "customer" or "admin"
    private String profilePhoto; // can be null

    // Full constructor
    public User(long userId, String email, String password, String firstName, String lastName, String gender,
                String phone, String country, String city, String role, String profilePhoto) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.country = country;
        this.city = city;
        this.role = role;
        this.profilePhoto= profilePhoto;
    }

    // Convenience constructor for inserts (ID auto-generated)
    public User(String email, String password, String firstName, String lastName, String gender, String phone,
                String country, String city, String role, String profilePhoto) {
        this(0, email, password, firstName, lastName, gender, phone, country, city, role, profilePhoto);
    }

    // Getters
    public long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getRole() {
        return role;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    // Setters


    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", role='" + role + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }
}