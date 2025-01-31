package com.example.demo.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

	List<Orders> findByCustomerId(Long customerId);

	@Query("SELECT SUM(o.orderAmount) FROM Orders o WHERE o.customer.id = :customerId")
	Double getTotalAmountSpentByCustomer(@Param("customerId") Long customerId);



	List<Orders> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
}
