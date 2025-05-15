package com.example.crud_mysql.model;

import com.example.crud_mysql.model.Enum.LifeCycle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
// @Table(name="truck") only if need to specify other table name
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String chassisNumber;

    @Column(unique = true, nullable = false)
    private String plate;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private LifeCycle lifeCycle = LifeCycle.READY;


    @ManyToOne()
    @JoinColumn(name = "deliverTruck_id", nullable = false)
    private DeliveryTruck deliveryTruck;

}
