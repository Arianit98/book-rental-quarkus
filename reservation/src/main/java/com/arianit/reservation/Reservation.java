package com.arianit.reservation;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Reservation extends PanacheEntity {
    public Long costumerId;
    public Long bookId;
    public String createdDate;
    public int durationInDays;
}
