package com.hybris.ciklum.service.impl;

import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.repository.OrderRepository;
import com.hybris.ciklum.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private static long counter = 1;
    private final OrderRepository orderRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:MM");

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrUpdate(Order order) {
        if (order.getId() != null) {
            Optional<Order> orderOptional = orderRepository.findById(order.getId());

            if (orderOptional.isPresent()) {
                Order newOrder = orderOptional.get();
                newOrder.setStatus(order.getStatus());
                newOrder.setUserId(order.getUserId());
                newOrder.setCreatedAt(LocalDateTime.now().format(formatter));
                return orderRepository.save(newOrder);
            }
        }
        Order creteOrder = new Order();
        creteOrder.setUserId(counter++);
        creteOrder.setCreatedAt(LocalDateTime.now().format(formatter));
        return orderRepository.save(creteOrder);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(("No order was found by id=" + id)));
    }

}
