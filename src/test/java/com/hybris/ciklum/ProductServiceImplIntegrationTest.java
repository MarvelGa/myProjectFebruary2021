package com.hybris.ciklum;

import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.OrderItemRepository;
import com.hybris.ciklum.repository.OrderRepository;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.ProductService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@Transactional
@SpringBootTest(classes = {Application.class})
public class ProductServiceImplIntegrationTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductService service;

    Order order;
    Product product;
    OrderItem orderItem;
    List<Product> listProducts;

    @BeforeEach
    void before() {
        service = new ProductServiceImpl(productRepository);
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
    public void shouldGetAllProducts() {
        List<Product> expected = listProducts;
        List<Product> actual = service.getAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Rollback
    public void shouldThrowExceptionIfNoProductWasFoundById() {
        Long id = 20000L;
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.getProductById(id);
        });

        assertEquals(exception.getMessage(), "No product was found by id=" + id);
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }

    @Rollback
    @Test
    public void shouldGetProductById() {
        Product expected = product;
        Product actual = service.getProductById(product.getId());
        Assertions.assertEquals(expected, actual);

    }

    @Rollback
    @Test
    public void shouldCreatProduct() {
        Product newProduct = Product.builder()
                .name("SmartPhone")
                .price(2500L)
                .status(Product.ProductsStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .build();
        Product actual = service.createOrUpdate(newProduct);
        Assertions.assertNotNull(actual);
    }

    @Rollback
    @Test
    public void shouldUpdateProduct() {
        Optional<Product> updateProduct = productRepository.findById(product.getId());
        updateProduct.get().setStatus(Product.ProductsStatus.OUT_OF_STOCK);
        Product actual = service.createOrUpdate(updateProduct.get());
        Assertions.assertEquals(updateProduct.get(), actual);
    }

    @Test
    @Rollback
    public void shouldThrowExceptionIfProductWasFoundByIdAndCanNotBeDeletedById() {

        Throwable exception = assertThrows(EntityNotFoundException.class, () -> {
            service.deleteProductById(555555L);
        });

        assertEquals(exception.getMessage(), "No product exist for given id");
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }


    @Rollback
    @Test
    public void shouldGetEmptyListIfOrderNotFounded() {
        List<Product> actual = service.getAllProductsByOrderId(55555L);
        Assertions.assertEquals(new ArrayList<>(), actual);
    }

    @Rollback
    @Test
    public void shouldGetAllProductsByOrderId() {
        List<Product> expected = new ArrayList<>();
        expected.add(product);
        List<Product> actual = service.getAllProductsByOrderId(order.getId());
        Assertions.assertEquals(expected, actual);
    }

    //    @Rollback
//    @Test
//    public void shouldDeleteProductById() {
//                verify(service).deleteProductById(product.getId());
//    }

    //    @Rollback
//    @Test
//    public void shouldDeleteALLProduct() {
//        verify(service).deleteAll();
//    }
}
