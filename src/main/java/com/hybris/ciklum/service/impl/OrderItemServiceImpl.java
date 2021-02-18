package com.hybris.ciklum.service.impl;

import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.OrderItemRepository;
import com.hybris.ciklum.repository.OrderRepository;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderItem createOrUpdate(OrderItem orderItem) {
        if (orderItem.getId() != null) {
            Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItem.getId());

            if (orderItemOptional.isPresent()) {
                OrderItem newOrderItem = orderItemOptional.get();
                newOrderItem.setQuantity(orderItem.getQuantity());
                return orderItemRepository.save(newOrderItem);
            }
        }
        OrderItem creteOrderItem = new OrderItem();
        creteOrderItem.setQuantity(orderItem.getQuantity());
        return orderItemRepository.save(creteOrderItem);
    }

    @Override
    public boolean addProductToOrderItem(Product product, OrderItem orderItem) {
        if (orderItem.getId() == null) {
            Product productEntity = productRepository.getOne(product.getId());
            if (!orderItemRepository.findFirstByIdAndProduct(orderItem.getId(), productEntity).isPresent()) {
                orderItem.setProduct(productEntity);
                orderItemRepository.save(orderItem);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean addOrderToOrderItem(Order order, OrderItem orderItem) {
        if (orderItem.getId() != null) {
            Order orderEntity = orderRepository.getOne(order.getId());
            if (order.getId() != null) {
                orderItem.setOrder(orderEntity);
                orderItemRepository.save(orderItem);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long id) {
        List<OrderItem> orderItems = orderItemRepository.getAllOrderItemsByOrderId(id);
        return orderItems.isEmpty() ? new ArrayList<>() : orderItems;
    }

    @Transactional
    @Override
    public List<OrderItem> getAllOrderByDESC() {
        List<OrderItem> orderItems = orderItemRepository.findAllOrderByDESC();
        return orderItems.isEmpty() ? new ArrayList<>() : orderItems;
    }

    @Transactional
    @Override
    public List<OrderItem> getAllOrderByASC() {
        List<OrderItem> orderItems = orderItemRepository.findAllOrderByASC();
        return orderItems.isEmpty() ? new ArrayList<>() : orderItems;
    }

    @Transactional
    @Override
    public List<OrderItem> getAllOrderByEntrance() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        return orderItems.isEmpty() ? new ArrayList<>() : orderItems;
    }

    @Override
    public OrderItem getOrderItemByOrderIdAndProductId(Long orderId, Long productId) {
        OrderItem orderItem = orderItemRepository.getOrderItemByOrderIdAndProductId(orderId, productId);
        if (orderItem != null) {
            return orderItem;
        }
        throw new EntityNotFoundException(("No order-item was found by orderId=" + orderId + " and by productId=" + productId));
    }

    @Override
    public boolean deleteOrderItemAndOrder(OrderItem orderItem, Order order) {
        if (orderItem.getId() != null || order.getId() != null) {
            OrderItem orderItemEntity = orderItemRepository.getOne(orderItem.getId());
            Order orderEntity = orderRepository.getOne(order.getId());
            orderItemRepository.delete(orderItemEntity);
            orderRepository.delete(orderEntity);
            return true;
        }
        return false;
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(("No order-item was found by id=" + id)));
    }

    @Override
    public void deleteOrderItemById(Long id) {
        Optional<OrderItem> orderItem = orderItemRepository.findById(id);
        if (orderItem.isPresent()) {
            orderItemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("No orderItem exist for given id");
        }
    }

    @Override
    public int getTotalPrice(Long orderId) {
        int totalPrice = 0;
        List<OrderItem> orderItems = orderItemRepository.getAllOrderItemsByOrderId(orderId);
        if (orderItems != null) {
            for (OrderItem elem : orderItems) {
                totalPrice += elem.getProduct().getPrice() * elem.getQuantity();
            }
        }
        return totalPrice;
    }

    @Override
    public void deleteAllOderItemsAndOrderByOrderId(Long orderId) {
        if (orderId != null) {
            Optional<Order> order = orderRepository.findById(orderId);

            if (order.isPresent()) {
                List<OrderItem> orderItemEntity = orderItemRepository.getAllOrderItemsByOrderId(orderId);
                orderItemRepository.deleteAll(orderItemEntity);
                orderRepository.delete(order.get());
            } else {
                throw new EntityNotFoundException("No order exist for given id");
            }

        }
    }

}
