package com.example.demo.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.common.AuthUtil;
import com.example.demo.models.Customer;
import com.example.demo.models.Orders;
import com.example.demo.services.CustomerService;
import com.example.demo.services.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	CustomerService customerService;

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostMapping
	public ResponseEntity<?> placeOrders(@RequestBody List<Orders> orders) {
	    Long userId = AuthUtil.getCurrentUserId();
	    List<Orders> placedOrders = orderService.placeOrders(orders, userId);
	    return ResponseEntity.ok(placedOrders);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<Orders> updateOrderStatus(@PathVariable("id") Long id,
			@RequestParam("status") String status) {
		Orders updatedOrder = orderService.updateOrderStatus(id, status);
		if (updatedOrder != null) {
			return ResponseEntity.ok(updatedOrder);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<List<Orders>> getAllOrders() {
		return ResponseEntity.ok(orderService.getAllOrders());
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<Orders>> getOrdersByCustomer(@PathVariable("customerId") Long customerId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		boolean isAdmin = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
		boolean isUser = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
		if (isAdmin) {
			return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
		}
		if (isUser) {
			Long userId = AuthUtil.getCurrentUserId();
			Customer customer = customerService.getCustomerById(customerId);
			if (!userId.equals(customer.getUserId()))
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only view your own orders");

			return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/orders-by-date-range")
	public ResponseEntity<List<Orders>> getOrdersByDateRange(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
		List<Orders> orders = orderService.getOrdersByDateRange(startDate, endDate);
		return ResponseEntity.ok(orders);
	}

	
	@GetMapping("/customer/{customerId}/total-amount")
	public ResponseEntity<Double> getTotalAmountSpentByCustomer(@PathVariable("customerId") Long customerId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
		}

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		boolean isAdmin = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
		boolean isUser = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
		if (isAdmin) {
			return ResponseEntity.ok(orderService.getTotalAmountSpentByCustomer(customerId));
		}

		if (isUser) {
			Long userId = AuthUtil.getCurrentUserId();
			Customer customer = customerService.getCustomerById(customerId);
			if (!userId.equals(customer.getUserId()))
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only view your own orders");

			return ResponseEntity.ok(orderService.getTotalAmountSpentByCustomer(customerId));
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
	}

}
