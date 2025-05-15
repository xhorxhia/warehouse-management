package com.example.crud_mysql;

import com.example.crud_mysql.model.Enum.OrderStatus;
import com.example.crud_mysql.model.Enum.Role;
import com.example.crud_mysql.model.Order;
import com.example.crud_mysql.model.OrderItem;
import com.example.crud_mysql.model.User;
import com.example.crud_mysql.repository.OrderItemRepository;
import com.example.crud_mysql.repository.OrderRepository;
import com.example.crud_mysql.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @InjectMocks
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemRepository orderItemRepository;

    @Test
    public void testCreateOrder_success() {
        User client = new User();
        client.setRoles(Set.of(Role.CLIENT));

        OrderItem item1 = new OrderItem();
        OrderItem item2 = new OrderItem();
        List<OrderItem> orderItems = List.of(item1, item2);

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        when((Publisher<?>) orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(client, orderItems);

        assertNotNull(result);
        assertEquals(Optional.of(1L), result.getId());
        assertEquals(OrderStatus.CREATED, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(orderItems);
    }
    @Test
    public void testCreateOrder_Fails_WhenUserNotClient() {
        User user = new User(); // No CLIENT role
        user.setRoles(Set.of(Role.SYSTEM_ADMIN));

        List<OrderItem> orderItems = List.of(new OrderItem());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(user, orderItems);
        });

        assertEquals("User is not a client", exception.getMessage());
    }

    @Test
    public void testCreateOrder_Fails_WhenEmptyOrderItems() {
        User client = new User();
        client.setRoles(Set.of(Role.CLIENT));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(client, Collections.emptyList());
        });

        assertEquals("Client and order items must not be null or empty", exception.getMessage());
    }

}
