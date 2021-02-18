package com.hybris.ciklum.service;

import com.hybris.ciklum.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAll();
    Product getProductById(Long id);
    Product createOrUpdate(Product product);
    void deleteProductById(Long id);
    void deleteAll();
    List<Product> getAllProductsByOrderId(Long id);
}
