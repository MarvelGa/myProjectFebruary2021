package com.hybris.ciklum.service;

import com.hybris.ciklum.model.Order;

public interface OrderService {
    Order createOrUpdate(Order order);
    Order getOrderById(Long id);
}
