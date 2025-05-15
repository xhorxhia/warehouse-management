package com.example.crud_mysql.controller;

import com.example.crud_mysql.model.DTO.DeliveryScheduleDTO;
import com.example.crud_mysql.model.Delivery;
import com.example.crud_mysql.response.MessageResponse;
import com.example.crud_mysql.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> schedule(@RequestBody DeliveryScheduleDTO deliveryScheduleDTO) {
          deliveryService.scheduleDelivery(deliveryScheduleDTO.getOrderId(),
                                          deliveryScheduleDTO.getTruckIds(),
                                          deliveryScheduleDTO.getDeliveryDate());

            return ResponseEntity.ok("Delivery scheduled successfully!");
    }
}
