package com.hybris.ciklum;

import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.OrderItemRepository;
import com.hybris.ciklum.repository.OrderRepository;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.OrderItemService;
import com.hybris.ciklum.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Transactional
@SpringBootTest(classes = {Application.class})
public class OrderItemServiceImplIntegrationTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderItemService orderItemService;

    Order order;
    Order order2;
    Product product;
    Product newProduct;
    OrderItem orderItem;
    OrderItem orderItem2;
    OrderItem newOrderItem;
    List<Product> listProducts;

    @BeforeEach
    void before() {
        orderItemService = new OrderItemServiceImpl(orderItemRepository, orderRepository, productRepository);
        newProduct = Product.builder()
                .name("Monitor")
                .price(1000L)
                .status(Product.ProductsStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .build();

        productRepository.save(newProduct);

        product = Product.builder()
                .name("Monitor")
                .price(1000L)
                .status(Product.ProductsStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .build();

        productRepository.save(product);
        listProducts = productRepository.findAll();


        order = Order.builder()
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(Order.OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(20)
                .build();
        orderItemRepository.save(orderItem);

        newOrderItem = OrderItem.builder()
                .quantity(20)
                .product(product)
                .build();
                orderItemRepository.save(newOrderItem);

      order2 = Order.builder()
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(Order.OrderStatus.PENDING)
                .build();

       orderItem2 = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(20)
                .build();
    }

    @Rollback
    @Test
    public void shouldCreatOrderItem() {
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(20)
                .build();

        OrderItem actual = orderItemService.createOrUpdate(orderItem);
        Assertions.assertNotNull(actual);
    }

    @Rollback
    @Test
    public void shouldUpdateOrderItem() {
        Optional<OrderItem> updateOrderItem = orderItemRepository.findById(orderItem.getId());
        updateOrderItem.get().setQuantity(20);
        OrderItem actual = orderItemService.createOrUpdate(updateOrderItem.get());
        Assertions.assertEquals(updateOrderItem.get(), actual);
    }

    @Rollback
    @Test
    public void shouldAddProductToOrderItem() {
        OrderItem  newOrderItem = OrderItem.builder()
                .quantity(20)
                .build();

        Boolean actual = orderItemService.addProductToOrderItem(product, newOrderItem);
        Assertions.assertTrue(actual);
    }

    @Rollback
    @Test
    public void shouldNotAddProductToOrderItem() {
        Boolean actual = orderItemService.addProductToOrderItem(product, orderItem);
        Assertions.assertFalse(actual);
    }

    @Rollback
    @Test
    public void shouldAddOrderToOrderItem() {
        Order newOrder = Order.builder()
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(Order.OrderStatus.PENDING)
                .build();

        Boolean actual = orderItemService.addOrderToOrderItem(order, newOrderItem);
        Assertions.assertTrue(actual);
    }

    @Rollback
    @Test
    public void shouldNotAddOrderToOrderItem() {
        Boolean actual = orderItemService.addOrderToOrderItem(order2, orderItem2);
        Assertions.assertFalse(actual);
    }


    @Test
    public void shouldGetOrderItemsByOrderId() {
        List<OrderItem> actual = orderItemService.getOrderItemsByOrderId(order.getId());
        Assertions.assertNotNull(actual);
    }

    @Test
    public void shouldGetAllOrderByDESC() {
        List<OrderItem> actual = orderItemService.getAllOrderByDESC();
        Assertions.assertNotNull(actual);
    }

    @Test
    public void shouldGetAllOrderByASC() {
        List<OrderItem> actual = orderItemService.getAllOrderByASC();
        Assertions.assertNotNull(actual);
    }

    @Test
    public void shouldGetAllOrderByEntrance() {
        List<OrderItem> actual = orderItemService.getAllOrderByEntrance();
        Assertions.assertNotNull(actual);
    }

    @Test
    public void shouldGetOrderItemByOrderIdAndProductId() {
        OrderItem actual = orderItemService.getOrderItemByOrderIdAndProductId(order.getId(), product.getId());
        Assertions.assertEquals(orderItem, actual);
    }

    @Test
    @Rollback
    public void shouldThrowExceptionIfNoOrderItemByOrderIdAndProductIdWasFoundById() {
        Long orderId = 20000L;
        Long productId = 20000L;
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            orderItemService.getOrderItemByOrderIdAndProductId(orderId, productId);
        });

        assertEquals(exception.getMessage(), "No order-item was found by orderId=" + orderId +" and by productId=" + productId);
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }

    @Rollback
    @Test
    public void shouldDeleteOrderItemAndOrder() {
        Boolean actual = orderItemService.deleteOrderItemAndOrder(orderItem, order);
        Assertions.assertTrue(actual);
    }

    @Rollback
    @Test
    public void shouldNotDeleteOrderItemAndOrder() {
        Boolean actual = orderItemService.deleteOrderItemAndOrder(orderItem2, order2);
        Assertions.assertFalse(actual);
    }

    @Test
    public void shouldGetOrderItemById() {
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

    @Rollback
    @Test
    public void shouldGetTotalPrice() {
        int expected = 20000;
        int actual = orderItemService.getTotalPrice(order.getId());
        Assertions.assertEquals(expected, actual);
    }
}
