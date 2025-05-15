package com.example.crud_mysql.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name="delivery_truck")
public class DeliveryTruck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // N deliverOrd have 1 delivery
    @ManyToOne
    @JoinColumn(name = "delver_id", nullable = false)
    private Delivery delivery;

    // N delivertr has 1 truck
    @ManyToOne
    @JoinColumn(name = "truck_id", nullable = false)
    private Truck truck;

}
