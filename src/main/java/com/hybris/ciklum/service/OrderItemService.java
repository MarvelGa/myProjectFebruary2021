package com.hybris.ciklum.service;

import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;

import java.util.List;

public interface OrderItemService {
    OrderItem createOrUpdate(OrderItem orderItem);
    boolean addProductToOrderItem(Product product, OrderItem orderItem);
    boolean addOrderToOrderItem(Order order, OrderItem orderItem);
    List<OrderItem> getOrderItemsByOrderId(Long id);
    List<OrderItem> getAllOrderByDESC();
    List<OrderItem> getAllOrderByASC();
    List<OrderItem> getAllOrderByEntrance();
    OrderItem getOrderItemByOrderIdAndProductId(Long orderId, Long productId);
    boolean deleteOrderItemAndOrder (OrderItem orderItem, Order order);
    OrderItem getOrderItemById (Long id);
    void deleteOrderItemById (Long id);
    int getTotalPrice(Long orderId);
    void deleteAllOderItemsAndOrderByOrderId(Long orderId);
}
