package com.example.crud_mysql.service;

import com.example.crud_mysql.model.*;
import com.example.crud_mysql.model.Enum.DeliveryStatus;
import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Enum.OrderStatus;
import com.example.crud_mysql.repository.DeliveryTruckRepository;
import com.example.crud_mysql.repository.DeliveryRepository;
import com.example.crud_mysql.repository.OrderRepository;
import com.example.crud_mysql.repository.TruckRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryTruckRepository deliveryTruckRepository;
    private final OrderRepository orderRepository;
    private final TruckRepository truckRepository;

    public DeliveryService(DeliveryRepository deliveryRepository, DeliveryTruckRepository deliveryTruckRepository, OrderRepository orderRepository, TruckRepository truckRepository) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryTruckRepository = deliveryTruckRepository;
        this.orderRepository = orderRepository;
        this.truckRepository = truckRepository;
    }


    Delivery getDeliveryByID(Long id){
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No delivery found with id: " + id));
    }

    public void completeDelivery(Long id){
        Delivery delivery = getDeliveryByID(id);

        if(delivery.getStatus().equals(DeliveryStatus.FULLFILLED)){
            throw new IllegalArgumentException("Delivery has already been fullfilled");
        }
        // change statuses
        delivery.setStatus(DeliveryStatus.FULLFILLED);
        deliveryRepository.save(delivery);

        Order order = delivery.getOrder();
        order.setStatus(OrderStatus.FULFILLED);
        orderRepository.save(order);

        // generate order number
        generateOrderNumber(order);
    }

    public void generateOrderNumber(Order order) {
        String generateUUIDNo = String.format("%010d",new BigInteger(UUID.randomUUID().toString().replace("-",""),16));
        String unique_no = generateUUIDNo.substring( generateUUIDNo.length() - 10);
        order.setOrderNumber(unique_no);
        orderRepository.save(order);
    }


    // schedule delivery for one order
    public void scheduleDelivery(Long orderId, List<Long> truckIds, Date deliveryDate){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No order found with id: " + orderId));

        int totalItems = order.getOrderItems().stream().mapToInt(OrderItem::getQuantity).sum();

        // check if  order is Approved and if it has max 10 items
        if(!order.getStatus().equals(OrderStatus.APPROVED) && totalItems>10){
            throw new IllegalArgumentException("Order is not approved");
        }

        List<Truck> readyTrucks = new ArrayList<>();

        for(Long truckId : truckIds){
            // get truck and check if is ready (truck does 1 delivery per day)
            Truck truck = truckRepository.findById(truckId)
                    .orElseThrow(() -> new IllegalArgumentException("No truck found with id: " + truckId));

            if(!truck.getLifeCycle().equals(LifeCycle.READY)) {
                throw new IllegalArgumentException("Truck is not ready");
            }

            // check if truck is free
            if (deliveryTruckRepository.existsByTruckAndDeliveryDate(truck.getId(), deliveryDate)) {
                throw new IllegalArgumentException("Truck has already been assigned to delivery");
            }

            readyTrucks.add(truck);
        }

        //check if deliveryDate is Sunday
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deliveryDate);
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            throw new IllegalArgumentException("Sunday is not allowed for delivery");
        }

        // Create delivery for each truck
        for(Truck truck : readyTrucks){
            Delivery delivery = new Delivery();
            delivery.setDelivery_date(deliveryDate);
            delivery.setOrder(order);
            deliveryRepository.save(delivery);

            // Create deliveryTruck
            DeliveryTruck deliveryTruck = new DeliveryTruck();
            deliveryTruck.setDelivery(delivery);
            deliveryTruck.setTruck(truck);
            deliveryTruckRepository.save(deliveryTruck);

            completeDelivery(delivery.getId());
        }

    }


    public Delivery createDelivery (User user, List<DeliveryTruck> deliveryTrucks) {
        if(!user.getRoles().contains("ROLE_WAREHOUSE_MANAGER")){
            throw new IllegalArgumentException("Not a warehouse manager");
        }

        // Create delivery
        Delivery delivery = new Delivery();
        delivery.setUser(user);
        delivery.setDelivery_date(new Date());
        delivery.setDelivery_gr_id(null);
        delivery.setStatus(DeliveryStatus.FULLFILLED);

        delivery = deliveryRepository.save(delivery);

        for(DeliveryTruck deliveryTruck : deliveryTrucks){
            deliveryTruck.setDelivery(delivery);
        }
        deliveryTruckRepository.saveAll(deliveryTrucks);

        delivery.setDeliveryTruck(new HashSet<>(deliveryTrucks));
        return delivery;
    }



}
