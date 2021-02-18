package com.hybris.ciklum;

import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.OrderItemRepository;
import com.hybris.ciklum.repository.OrderRepository;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.OrderService;
import com.hybris.ciklum.service.ProductService;
import com.hybris.ciklum.service.impl.OrderServiceImpl;
import com.hybris.ciklum.service.impl.ProductServiceImpl;
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

@Transactional
@SpringBootTest(classes = {Application.class})
public class OrderServiceImplIntegrationTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderService service;

    Order order;
    Product product;
    OrderItem orderItem;
    List<Product> listProducts;

    @BeforeEach
    void before() {
        service = new OrderServiceImpl(orderRepository);
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
    }

    @Rollback
    @Test
    public void shouldCreatOrder() {
        Order order = Order.builder()
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(Order.OrderStatus.PENDING)
                .build();

        Order actual = service.createOrUpdate(order);
        Assertions.assertNotNull(actual);
    }

    @Rollback
    @Test
    public void shouldUpdateOrder() {
        Optional<Order> updateOrder = orderRepository.findById(order.getId());
        updateOrder.get().setStatus(Order.OrderStatus.PENDING);
        Order actual = service.createOrUpdate(updateOrder.get());
        Assertions.assertEquals(updateOrder.get(), actual);
    }

    @Test
    @Rollback
    public void shouldThrowExceptionIfNoOrderWasFoundById() {
        Long id = 20000L;
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.getOrderById(id);
        });

        assertEquals(exception.getMessage(), "No order was found by id=" + id);
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }

    @Rollback
    @Test
    public void shouldGetOrderById() {
        Order expected = order;
        Order actual = service.getOrderById(order.getId());
        Assertions.assertEquals(expected, actual);

    }
}
