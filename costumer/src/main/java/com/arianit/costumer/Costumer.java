package com.arianit.costumer;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity(name = "costumers")
public class Costumer extends PanacheEntity {
    public String name;
    public String email;
    public String phone;
    public String address;
    public int age;
}
