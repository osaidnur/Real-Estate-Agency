package com.example.a1210733_1211088_courseproject.models;

import java.time.LocalDateTime;

public class Reservation {
    private long reservationId;
    private long userId;
    private long propertyId;
    private LocalDateTime reservedDatetime;
    private String status; // "pending","confirmed","cancelled"

    public Reservation(long reservationId, long userId, long propertyId,
                       LocalDateTime reservedDatetime, String status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.propertyId = propertyId;
        this.reservedDatetime = reservedDatetime;
        this.status = status;
    }

    // For insert (ID auto-gen)
    public Reservation(long userId, long propertyId,
                       LocalDateTime reservedDatetime, String status) {
        this(0, userId, propertyId, reservedDatetime, status);
    }

    public long getReservationId() {
        return reservationId;
    }

    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
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

    public LocalDateTime getReservedDatetime() {
        return reservedDatetime;
    }

    public void setReservedDatetime(LocalDateTime reservedDatetime) {
        this.reservedDatetime = reservedDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", userId=" + userId +
                ", propertyId=" + propertyId +
                ", reservedDatetime=" + reservedDatetime +
                ", status='" + status + '\'' +
                '}';
    }
}
