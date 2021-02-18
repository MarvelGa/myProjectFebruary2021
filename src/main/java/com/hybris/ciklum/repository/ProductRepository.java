package com.hybris.ciklum.repository;

import com.hybris.ciklum.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT p.id, p.name, p.price, p.status, p.created_at from  products p JOIN order_item oi ON oi.product_id = p.id WHERE oi.order_id =?", nativeQuery = true)
    List<Product> getAllProductByOrderId(Long id);
}
