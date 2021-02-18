package com.hybris.ciklum.repository;

import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findFirstByIdAndProduct(Long id, Product product);

    @Query(value = "SELECT * from order_item  WHERE order_id =?", nativeQuery = true)
    List<OrderItem> getAllOrderItemsByOrderId(Long id);

    @Query(value = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity FROM order_item oi  WHERE oi.order_id =? AND oi.product_id=?", nativeQuery = true)
    OrderItem getOrderItemByOrderIdAndProductId(Long orderId, Long productId);

    @Query(value = "SELECT * from order_item  ORDER BY quantity DESC", nativeQuery = true)
    List<OrderItem> findAllOrderByDESC();

    @Query(value = "SELECT * from order_item  ORDER BY quantity ASC ", nativeQuery = true)
    List<OrderItem> findAllOrderByASC();
}
