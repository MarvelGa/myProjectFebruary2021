package com.hybris.ciklum.controller;

import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.ProductService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Data
public class ProductController {
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping("/create-product")
    public String createProduct(Model model) {
        model.addAttribute("product", new Product());
        return "create-product";
    }

    @GetMapping("/products")
    public String getAllProducts(Model model) {
        List<Product> products = productService.getAll();
        model.addAttribute("products", products);
        return "products";
    }

    @PostMapping("/products")
    public String createProduct(@Validated @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error(result.getAllErrors().toString());
            return "create-product";
        }
        productService.createOrUpdate(product);
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "update-product";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable long id, @ModelAttribute Product product, BindingResult result) {
        if (result.hasErrors()) {
            logger.error(result.getAllErrors().toString());
            return "update-product";
        }
        productService.createOrUpdate(product);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/all")
    public String deleteAllProducts() {
        productService.deleteAll();
        return "redirect:/products";
    }

}
