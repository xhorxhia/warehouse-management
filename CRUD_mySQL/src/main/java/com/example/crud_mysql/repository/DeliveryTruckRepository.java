package com.example.crud_mysql.repository;

import com.example.crud_mysql.model.DeliveryTruck;
import com.example.crud_mysql.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DeliveryTruckRepository extends JpaRepository<DeliveryTruck, Long> {

    // check if truck has already been assigned to a delivery (1 truck only one delivery in a day)
    @Query("SELECT COUNT(dt) > 0 FROM DeliveryTruck dt " +  //true if at least one matching DeliveryTruck record exists
            "JOIN dt.delivery d " +                         // because dt has a delivery propety
            "WHERE dt.truck.id = :truckId AND d.delivery_date = :deliveryDate")
    boolean existsByTruckAndDeliveryDate(@Param("truckId") Long truckId,
                                         @Param("deliveryDate") Date deliveryDate);



    //  get all trucks assigned to deliveries for a specific date
    @Query("SELECT dt.truck" +
            " FROM DeliveryTruck dt " +
            "WHERE dt.delivery.delivery_date = :date")
    List<DeliveryTruck> findByDeliveryDate(@Param("date") Date date);


    // count how many deliveries a truck has completed
    @Query("SELECT count (dt)" +
            "FROM DeliveryTruck dt JOIN Delivery d " +
            "WHERE d.status = 'FULLFILLED'")
    Long countTrucksByStatus(@Param("status") String status);


    // get the latest delivery date for a truck
    @Query("SELECT MAX (d.delivery_date) " +
            "FROM DeliveryTruck dt JOIN dt.delivery d " +
            "WHERE dt.truck.id =: truckId")
    Long findMaxDeliveryDateByTruckId(@Param("truckId") Long truckId);


    // the total number of trucks assigned to a specific delivery
    @Query("SELECT COUNT (dt.truck)" +
            "FROM DeliveryTruck dt JOIN dt.delivery d " +
            "WHERE d.id = :deliveryId")
    Long countTrucksByDeliveryId(@Param("deliveryId") Long deliveryId);




}
