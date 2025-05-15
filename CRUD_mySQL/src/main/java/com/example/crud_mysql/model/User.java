package com.example.crud_mysql.model;


import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Enum.Role;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@Table(name= "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    private LifeCycle lifeCycle = LifeCycle.READY;

    // mapped with user field in Order  1 user - N orders
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();
}
