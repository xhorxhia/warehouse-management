package com.example.crud_mysql.model.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DeliveryScheduleDTO {
    private Long orderId;
    private List<Long> truckIds;
    private Date deliveryDate;
}
