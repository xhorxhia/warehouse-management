package com.example.crud_mysql.repository;

import com.example.crud_mysql.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // check if a given order ID has an ongoing delivery (UNDER_DELIVERY)
    @Query("SELECT COUNT (d)>0" +
            " FROM Delivery d " +
            "WHERE d.order.id = :orderId AND d.status = 'UNDER_DELIVERY'")
    boolean existsByOrderId(@Param("orderId") Long orderId);


}
