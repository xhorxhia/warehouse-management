package com.example.crud_mysql.repository;

import com.example.crud_mysql.model.Enum.OrderStatus;
import com.example.crud_mysql.model.Order;
import com.example.crud_mysql.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // find all orders that haven’t been delivered yet
    @Query("SELECT o " +
            "FROM Order o " +
            "WHERE o.status !='FULLFILLED' AND o.status != 'CALCELED'")
    List<Order> findAllNotFullfilled();

    // all orders that have been delivered in the last 7 days
    @Query("SELECT o " +
            "FROM Order o JOIN o.delivery d " +
            "WHERE d.delivery_date >= :last7daysDate")
    List<Order> findAllByLast7daysDate(@Param("last7daysDate")Date last7daysDate);

    // Client can see only his orders
    @Query("SELECT o " +
            "FROM Order o " +
            "WHERE o.user =: userId AND o.status  =: status")
    List<Order> fetchByUserAndStatus(@Param("userId") Long userId,@Param("status") OrderStatus status);


    // For WAREHOUSE_MANAGER: Fetch all orders but only selected fields
    @Query("SELECT  o.id, o.status, o.submittedDate " +
            "FROM Order o " +
            "WHERE o.status = :status " +
            "ORDER BY o.submittedDate DESC ")
    List<Order> findByStatus(@Param("status") OrderStatus status);
    
}
