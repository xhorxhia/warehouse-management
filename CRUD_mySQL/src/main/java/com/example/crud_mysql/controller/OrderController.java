package com.example.crud_mysql.controller;

import com.example.crud_mysql.model.DTO.OrderRequestDTO;
import com.example.crud_mysql.model.Enum.OrderStatus;
import com.example.crud_mysql.model.Order;
import com.example.crud_mysql.model.OrderItem;
import com.example.crud_mysql.model.User;
import com.example.crud_mysql.repository.UserRepository;
import com.example.crud_mysql.service.OrderService;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody OrderRequestDTO orderRequestDTO) {
        try{
            logger.info("Creating order for user " + orderRequestDTO.getUser().getUsername());
            orderService.createOrder(orderRequestDTO.getUser(), orderRequestDTO.getOrderItems());
            logger.info("Successfully created order");
            return ResponseEntity.ok("Order added successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error while creating order", e);
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody List<OrderItem> orderItems) {
        orderService.updateOrder(id, orderItems);
        return ResponseEntity.ok("Order updated successfully");
    }

    @PutMapping("/{orderNumber}/submit")
    public ResponseEntity<String> submitOrder(@PathVariable Long orderNumber) {
        orderService.submitOrder(orderNumber);
        return ResponseEntity.ok("Order submitted successfully.");
    }

    @PutMapping("/{orderNumber}/approve")
    public ResponseEntity<String> approveOrder(@PathVariable Long orderNumber) {
        orderService.approveOrder(orderNumber);
        return ResponseEntity.ok("Order approved successfully.");
    }

    @PutMapping("/{orderNumber}/decline")
    public ResponseEntity<String> declineOrder(@PathVariable Long orderNumber, @RequestBody String reason) {
        orderService.declineOrder(orderNumber, reason);
        return ResponseEntity.ok("Order declined successfully.");
    }

    @PutMapping("/{orderNumber}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderNumber) {
        orderService.cancelOrder(orderNumber);
        return ResponseEntity.ok("Order canceled successfully.");
    }

    @DeleteMapping("/{orderNumber}/soft-delete")
    public ResponseEntity<?> softDeleteOrder(@PathVariable("orderNumber") Long id){
        boolean result = orderService.deleteOrder(id);
        logger.info("Soft deleting order " + id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/userOrders")
    public ResponseEntity<List<Order>> getClientOrders(@RequestParam OrderStatus status){
       // get current user role
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found")) ;

        List<Order> orders = orderService.findAllClientOrders(status, user);
        return ResponseEntity.ok(orders);
    }


}
