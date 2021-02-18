package com.hybris.ciklum.unitTest;

import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.OrderItemRepository;
import com.hybris.ciklum.repository.OrderRepository;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.OrderItemService;
import com.hybris.ciklum.service.OrderService;
import com.hybris.ciklum.service.ProductService;
import com.hybris.ciklum.service.impl.OrderItemServiceImpl;
import com.hybris.ciklum.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderItemServiceImplUnitTest {
    @Autowired
    private ProductRepository productRepository = mock(ProductRepository.class);;
    @Autowired
    private OrderRepository orderRepository  = mock(OrderRepository.class);
    @Autowired
    private OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);

    @Autowired
    private OrderItemService orderItemService;

//    @Autowired
//    private OrderService orderService;
//    @Autowired
//    private ProductService productService;

    Order order;
    Order order2;
    Product product;
    OrderItem orderItem;
    OrderItem orderItem2;
    OrderItem newOrderItem;
    List<Product> listProducts;
    List<OrderItem> orderItems = new ArrayList<>();

    @BeforeEach
    void before() {
        orderItemService = new OrderItemServiceImpl(orderItemRepository, orderRepository, productRepository);

        product = Product.builder()
                .id(10L)
                .name("Monitor")
                .price(1000L)
                .status(Product.ProductsStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .build();

//        listProducts = productRepository.findAll();



        order = Order.builder()
                .id(10L)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(Order.OrderStatus.PENDING)
                .build();

        orderItem = OrderItem.builder()
                .id(10L)
                .order(order)
                .product(product)
                .quantity(20)
                .build();

        order2 = Order.builder()
                .id(12L)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(Order.OrderStatus.PENDING)
                .build();

        orderItem2 = OrderItem.builder()
                .id(20L)
                .order(order)
                .product(product)
                .quantity(20)
                .build();

        newOrderItem = OrderItem.builder()
                .id(44L)
                .quantity(20)
                .product(product)
                .build();

        orderItems.add(orderItem);
    }

    @Test
    public void shouldCreatOrUpdateOrderItem() {
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));
        orderItem.setQuantity(40);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);

        OrderItem  actual = orderItemService.createOrUpdate(orderItem);
        verify(orderItemRepository).save(orderItem);
        Assertions.assertEquals(orderItem, actual);
    }

    @Rollback
    @Test
    public void shouldAddProductToOrderItem() {
        OrderItem  newOrderItem = OrderItem.builder()
                .quantity(20)
                .build();
        when(productRepository.getOne(product.getId())).thenReturn(product);
        Boolean actual = orderItemService.addProductToOrderItem(product, newOrderItem);
        Assertions.assertTrue(actual);
    }

    @Rollback
    @Test
    public void shouldNotAddProductToOrderItem() {
        Product product = Product.builder()
                .name("Monitor")
                .price(1000L)
                .status(Product.ProductsStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .build();
        when(productRepository.getOne(product.getId())).thenReturn(product);
        Boolean actual = orderItemService.addProductToOrderItem(product, orderItem);
        Assertions.assertFalse(actual);
    }
//
    @Rollback
    @Test
    public void shouldAddOrderToOrderItem() {
        Order newOrder = Order.builder()
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(Order.OrderStatus.PENDING)
                .build();

        when(orderRepository.getOne(order.getId())).thenReturn(order);
        Boolean actual = orderItemService.addOrderToOrderItem(order, newOrderItem);
        Assertions.assertTrue(actual);
    }

    @Rollback
    @Test
    public void shouldNotAddOrderToOrderItem() {
        Order newOrder = Order.builder()
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(Order.OrderStatus.PENDING)
                .build();

        when(orderRepository.getOne(order.getId())).thenReturn(order);
        Boolean actual = orderItemService.addOrderToOrderItem(newOrder, orderItem2);
        Assertions.assertFalse(actual);
    }


    @Test
    public void shouldGetOrderItemsByOrderId() {
        when(orderItemRepository.getAllOrderItemsByOrderId(order.getId())).thenReturn(orderItems);
        List<OrderItem> actual = orderItemService.getOrderItemsByOrderId(order.getId());
        Assertions.assertNotNull(actual);
    }

    @Test
    public void shouldGetAllOrderByDESC() {
        when(orderItemRepository.findAllOrderByDESC()).thenReturn(orderItems);
        List<OrderItem> actual = orderItemService.getAllOrderByDESC();
        Assertions.assertNotNull(actual);
    }

    @Test
    public void shouldGetAllOrderByASC() {
        when(orderItemRepository.findAllOrderByASC()).thenReturn(orderItems);
        List<OrderItem> actual = orderItemService.getAllOrderByASC();
        Assertions.assertNotNull(actual);
    }

    @Test
    public void shouldGetAllOrderByEntrance() {
        when(orderItemRepository.findAll()).thenReturn(orderItems);
        List<OrderItem> actual = orderItemService.getAllOrderByEntrance();
        Assertions.assertNotNull(actual);
    }

    @Test
    public void shouldGetOrderItemByOrderIdAndProductId() {
        when(orderItemRepository.getOrderItemByOrderIdAndProductId(order.getId(), product.getId())).thenReturn(orderItem);
        OrderItem actual = orderItemService.getOrderItemByOrderIdAndProductId(order.getId(), product.getId());
        Assertions.assertEquals(orderItem, actual);
    }

    @Test
    public void shouldThrowExceptionIfNoOrderItemByOrderIdAndProductIdWasFoundById() {
        Long orderId = 20000L;
        Long productId = 20000L;
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.getOrderItemByOrderIdAndProductId(orderId, productId);
        });

        assertEquals(exception.getMessage(), "No order-item was found by orderId=" + orderId +" and by productId=" + productId);
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }


    @Test
    public void shouldDeleteOrderItemAndOrder() {
        when(orderItemRepository.getOrderItemByOrderIdAndProductId(order.getId(), product.getId())).thenReturn(orderItem);
        Boolean actual = orderItemService.deleteOrderItemAndOrder(orderItem, order);
        Assertions.assertTrue(actual);
    }

    @Test
    public void shouldNotDeleteOrderItemAndOrder() {
        Order newOrder = Order.builder()
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(Order.OrderStatus.PENDING)
                .build();

        OrderItem  newOrderItem = OrderItem.builder()
                .quantity(20)
                .build();

        when(orderItemRepository.getOrderItemByOrderIdAndProductId(newOrder.getId(), product.getId())).thenReturn(newOrderItem);
        Boolean actual = orderItemService.deleteOrderItemAndOrder(newOrderItem, newOrder);
        Assertions.assertFalse(actual);
    }

    @Test
    public void shouldGetOrderItemById() {
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.ofNullable(orderItem));
        OrderItem actual = orderItemService.getOrderItemById(orderItem.getId());
        Assertions.assertEquals(orderItem, actual);
    }

    @Test
    public void shouldThrowExceptionIfNoOrderItemWasFoundById() {
        Long orderItemId = 20000L;
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.getOrderItemById(orderItemId);
        });

        assertEquals(exception.getMessage(), "No order-item was found by id=" + orderItemId);
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }

    @Test
    public void shouldGetTotalPrice() {
        when(orderItemRepository.getAllOrderItemsByOrderId(order.getId())).thenReturn(orderItems);
        int expected = 20000;
        int actual = orderItemService.getTotalPrice(order.getId());
        Assertions.assertEquals(expected, actual);
    }
}
