package com.hybris.ciklum.service.impl;

import com.hybris.ciklum.exception.EntityNotFoundException;
import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();
        return products.isEmpty() ? new ArrayList<>() : products;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(("No product was found by id=" + id)));
    }

    @Override
    public Product createOrUpdate(Product product) {
        if (product.getId() != null) {
            Optional<Product> productOptional = productRepository.findById(product.getId());

            if (productOptional.isPresent()) {
                Product newProduct = productOptional.get();
                newProduct.setName(product.getName());
                newProduct.setPrice(product.getPrice());
                newProduct.setStatus(product.getStatus());
                newProduct.setCreatedAt(LocalDateTime.now());
                return productRepository.save(newProduct);
            }
        }
        Product creteProduct = new Product();
        creteProduct.setName(product.getName());
        creteProduct.setPrice(product.getPrice());
        creteProduct.setStatus(product.getStatus());
        creteProduct.setCreatedAt(LocalDateTime.now());
        return productRepository.save(creteProduct);
    }

    @PreAuthorize("hasAuthority('USER')")
    @Override
    public void deleteProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            productRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("No product exist for given id");
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    public void deleteAll() {
        productRepository.deleteAll();
    }

    @Override
    public List<Product> getAllProductsByOrderId(Long id) {
        List<Product> products = productRepository.getAllProductByOrderId(id);
        return products.isEmpty() ? new ArrayList<>() : products;
    }
}
