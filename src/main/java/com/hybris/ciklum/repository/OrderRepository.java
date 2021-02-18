package com.hybris.ciklum.repository;

import com.hybris.ciklum.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
