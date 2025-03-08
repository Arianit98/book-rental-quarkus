package com.arianit.reservation.client;

import lombok.Data;

@Data
public class Costumer {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private int age;
}
