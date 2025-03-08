package com.arianit.book;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity(name = "books")
public class Book extends PanacheEntity {
    public String title;
    public String author;
    public int year;
    public int stockNr;
    public int reservedNr = 0;
}
