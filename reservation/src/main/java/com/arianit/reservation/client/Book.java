package com.arianit.reservation.client;

import lombok.Data;

@Data
public class Book {

    private Long id;
    private String title;
    private String author;
    private int year;
    private int stockNr;
    private int reservedNr;
}
