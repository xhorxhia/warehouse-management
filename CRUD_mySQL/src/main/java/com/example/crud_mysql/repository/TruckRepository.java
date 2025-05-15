package com.example.crud_mysql.repository;

import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
    Page<Truck> findAllByLifeCycle(Pageable pageable, LifeCycle lifeCycle);



    // all trucks that have never been assigned to any delivery
    @Query("SELECT t " +
            "FROM Truck t " +
            "LEFT JOIN t.deliveryTruck dt " +
            "WHERE dt IS NULL ")
    Page<Truck> findAllByDeliveryTruck(Pageable pageable);




}
