package com.example.crud_mysql.model;

import com.example.crud_mysql.model.Enum.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date delivery_date;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private String delivery_gr_id;

    // N deliveries have 1 manager
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private User user;

    // 1 delivery - 1 order
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    // 1 deliver has N del-truck
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeliveryTruck> deliveryTruck;
}
