package com.example.crud_mysql.model;

import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Enum.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deadlineDate;

    @Enumerated(EnumType.STRING)
    private LifeCycle lifeCycle = LifeCycle.READY;

    private String declineReason;  // Reason for declining, if applicable

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User user;

    // 1 order - N orderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;

}
