package com.example.crud_mysql.model.DTO;

import com.example.crud_mysql.model.OrderItem;
import com.example.crud_mysql.model.User;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private User user;
    private List<OrderItem> orderItems;
}
