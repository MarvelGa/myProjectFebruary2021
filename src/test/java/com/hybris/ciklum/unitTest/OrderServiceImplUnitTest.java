package com.hybris.ciklum.unitTest;

import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.repository.OrderRepository;
import com.hybris.ciklum.service.OrderService;
import com.hybris.ciklum.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class OrderServiceImplUnitTest {
    @Autowired
    private OrderRepository orderRepository = mock(OrderRepository.class);
    @Autowired
    private OrderService service;

    Order order;

    @BeforeEach
    void before() {
        service = new OrderServiceImpl(orderRepository);

        order = Order.builder()
                .id(1L)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(Order.OrderStatus.PENDING)
                .build();
    }

    @Test
    public void shouldCreatOrUpdateOrder() {
        Order updateOrder = Order.builder()
                .id(2L)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(Order.OrderStatus.PENDING)
                .build();
        when(orderRepository.findById(updateOrder.getId())).thenReturn(Optional.of(updateOrder));
        when(orderRepository.save(updateOrder)).thenReturn(updateOrder);
        Order actual = service.createOrUpdate(updateOrder);
        verify(orderRepository).save(updateOrder);
        Assertions.assertEquals(updateOrder, actual);
    }


    @Test
    public void shouldThrowExceptionIfNoOrderWasFoundById() {
        Long id = 20000L;
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.getOrderById(id);
        });

        assertEquals(exception.getMessage(), "No order was found by id=" + id);
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }

    @Test
    public void shouldGetOrderById() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        Order expected = order;
        Order actual = service.getOrderById(order.getId());
        Assertions.assertEquals(expected, actual);

    }
}
