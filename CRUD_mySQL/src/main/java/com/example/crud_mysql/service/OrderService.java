package com.example.crud_mysql.service;

import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Enum.OrderStatus;
import com.example.crud_mysql.model.Enum.Role;
import com.example.crud_mysql.model.Order;
import com.example.crud_mysql.model.OrderItem;
import com.example.crud_mysql.model.User;
import com.example.crud_mysql.repository.OrderItemRepository;
import com.example.crud_mysql.repository.OrderRepository;
import com.example.crud_mysql.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    // client
    public Order createOrder(User client, List<OrderItem> orderItems) {
        if (client == null || orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Client and order items must not be null or empty");
        }

        if(!client.getRoles().contains(Role.CLIENT)) {
            throw new IllegalArgumentException("User is not a client");
        }

        // new order
        Order order = new Order();
        order.setUser(client);
        order.setSubmittedDate(new Date());
        order.setDeadlineDate(null);
        order.setStatus(OrderStatus.CREATED);

        orderRepository.save(order);

        for(OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
        }
        orderItemRepository.saveAll(orderItems);

        order.setOrderItems(new HashSet<>(orderItems));
        return order;
    }

// client
    public void updateOrder(Long orderId, List<OrderItem> updatedItems) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if(order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.DECLINED) {
            throw new IllegalArgumentException("Order can only be updated if it is in CREATED or DECLINED state.");
        }

        Set<OrderItem> currentItems = order.getOrderItems();

        for(OrderItem updatedItem : updatedItems) {
            boolean found = false;

            for(OrderItem currentItem : currentItems) {
                if(currentItem.getId().equals(updatedItem.getId())) {
                    currentItem.setQuantity(updatedItem.getQuantity()); // user modifies items in the order
                    found = true;
                    break;
                }
            }

            if(!found) { // user adds a new item in the order
                currentItems.add(updatedItem);
            }
        }

        // user removes items from the order, if no id matches
        currentItems.removeIf(currentItem -> updatedItems.stream()
                .noneMatch(updatedItem -> updatedItem.getItem().getId().equals(currentItem.getItem().getId())));

        // save updated order
        orderRepository.save(order);
    }

// client
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.FULFILLED
                || order.getStatus() == OrderStatus.UNDER_DELIVERY
                || order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("Order cannot be canceled if it is FULFILLED, UNDER_DELIVERY, or already CANCELED.");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    // client
    public void submitOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED
                && order.getStatus() != OrderStatus.DECLINED) {
           throw new IllegalArgumentException("Order can only be submitted if it is in CREATED or DECLINED state.");
        }

        order.setStatus(OrderStatus.AWAITING_APPROVAL);
        orderRepository.save(order);
    }

    public boolean deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setLifeCycle(LifeCycle.DELETED);
        orderRepository.save(order);
        return true;
    }

    // manager
    public void approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.AWAITING_APPROVAL) {
            throw new IllegalArgumentException("Order can only be Approved if it is in AWAITING APPROVAL state.");
        }

        order.setStatus(OrderStatus.APPROVED);
        orderRepository.save(order);
    }

     // manager
     public void declineOrder(Long orderId, String reason) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         User user = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

         if(!user.getRoles().contains(Role.WAREHOUSE_MANAGER)) {
             throw new IllegalArgumentException("User is not a warehouse manager");
         }

        Order order = orderRepository.findById(orderId)
                 .orElseThrow(() -> new IllegalArgumentException("Order not found"));

         if (order.getStatus() != OrderStatus.AWAITING_APPROVAL) {
             throw new IllegalArgumentException("Order can only be Declined if it is in AWAITING APPROVAL state.");
         }

         order.setStatus(OrderStatus.DECLINED);
         order.setDeclineReason(reason);
         orderRepository.save(order);
     }

    // Client can see only his orders and For WAREHOUSE_MANAGER: Fetch all orders but only selected fields
    public List<Order> findAllClientOrders(OrderStatus orderStatus, User user) {

        if((user.getRoles()).contains(Role.CLIENT)) {
            return orderRepository.fetchByUserAndStatus(user.getId(), orderStatus);
        }else if((user.getRoles()).contains(Role.WAREHOUSE_MANAGER)) {
            return orderRepository.findByStatus(orderStatus);
        }else {
            throw new IllegalArgumentException("Invalid role");
        }
    }



}
