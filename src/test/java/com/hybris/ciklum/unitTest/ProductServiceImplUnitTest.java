package com.hybris.ciklum.unitTest;

import com.hybris.ciklum.Application;
import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.ProductService;
import com.hybris.ciklum.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class ProductServiceImplUnitTest {
    @Autowired
    private ProductRepository repository = mock(ProductRepository.class);
    @Autowired
    private ProductService service;

    Product product;
    List<Product> listProducts = new ArrayList<>();

    @BeforeEach
    void before() {
        service = new ProductServiceImpl(repository);

        product = Product.builder()
                .name("Monitor")
                .price(1000L)
                .status(Product.ProductsStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .build();
        listProducts.add(product);
    }

    @Test
    public void shouldGetAllProducts() {
        when(repository.findAll()).thenReturn(listProducts);
        List<Product> expected = listProducts;
        List<Product> actual = service.getAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldThrowExceptionIfNoProductWasFoundById() {
        Long id = 20000L;

        Throwable exception = assertThrows(EntityNotFoundException.class, () -> {
            service.getProductById(id);
        });

        assertEquals(exception.getMessage(), "No product was found by id=" + id);
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }


    @Test
    public void shouldGetProductById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.ofNullable(product));
        Product expected = product;
        Product actual = service.getProductById(id);
        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void shouldUpdateOrCreatProduct() {
        Product updateProduct = Product.builder()
                .id(5L)
                .name("SmartPhone")
                .price(2500L)
                .status(Product.ProductsStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(5L)).thenReturn(Optional.of(updateProduct));
        when(repository.save(updateProduct)).thenReturn(updateProduct);
        Product actual = service.createOrUpdate(updateProduct);
        Assertions.assertEquals(updateProduct, actual);
    }


    @Test
    public void shouldThrowExceptionIfProductWasFoundByIdAndCanNotBeDeletedById() {

        Throwable exception = assertThrows(EntityNotFoundException.class, () -> {
            service.deleteProductById(null);
        });

        assertEquals(exception.getMessage(), "No product exist for given id");
        assertEquals(exception.getClass(), EntityNotFoundException.class);
    }

    @Test
    public void shouldGetAllProductsByOrderId() {
        List<Product> list = new ArrayList<>();
        Product product = Product.builder()
                .id(5L)
                .name("SmartPhone")
                .price(2500L)
                .status(Product.ProductsStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(1L)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(Order.OrderStatus.PENDING)
                .build();


        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .product(product)
                .quantity(20)
                .build();

        list.add(product);
        when(repository.getAllProductByOrderId(order.getId())).thenReturn(list);
        List<Product> actual = repository.getAllProductByOrderId(orderItem.getOrder().getId());
        Assertions.assertEquals(list, actual);
    }

    //    @Test
//    public void shouldDeleteAllProducts() {
//        verify(repository).deleteAll();
//        verify(service).deleteAll();
//    }

    //    @Test
//    public void shouldDeleteProductById() {
//        List <Product> list = new ArrayList<>();
//        Product product = Product.builder()
//                .id(5L)
//                .name("SmartPhone")
//                .price(2500L)
//                .status(Product.ProductsStatus.IN_STOCK)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        list.add(product);
//        when(repository.findById(product.getId())).thenReturn(Optional.of(product));
////        verify(repository).deleteById(product.getId());
//        verify(service).deleteProductById(product.getId());
//    }


}
