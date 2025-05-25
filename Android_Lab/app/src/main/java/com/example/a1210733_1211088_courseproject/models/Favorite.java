package com.example.a1210733_1211088_courseproject.models;

import java.time.LocalDateTime;

public class Favorite {
    private long userId;
    private long propertyId;
    private java.time.LocalDateTime addedAt;

    // Full constructor


    public Favorite(long userId, long propertyId, LocalDateTime addedAt) {
        this.userId = userId;
        this.propertyId = propertyId;
        this.addedAt = addedAt;
    }

    // Convenience for “now”:
    public Favorite(long userId, long propertyId) {
        this(userId, propertyId, java.time.LocalDateTime.now());
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "userId=" + userId +
                ", propertyId=" + propertyId +
                ", addedAt=" + addedAt +
                '}';
    }
}
