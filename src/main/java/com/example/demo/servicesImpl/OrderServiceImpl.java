package com.example.demo.servicesImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.exceptions.OrderNotFoundException;
import com.example.demo.models.Customer;
import com.example.demo.models.Orders;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.services.OrderService;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired 
    private CustomerRepository customerRepository;

    @Override
    public List<Orders> placeOrders(List<Orders> orders, Long userId) {
        Optional<Customer> customer = customerRepository.findByUserId(userId);
        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not registered as a customer");
        }

        return orders.stream().map(order -> {
            order.setCustomer(customer.get());
            order.setOrderDate(LocalDate.now());
            order.setStatus("PENDING");
            return orderRepository.save(order);
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Orders updateOrderStatus(Long id, String status) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Orders> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Orders> getOrdersByDateRange(String startDate, String endDate) {
        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            List<Orders> orders = orderRepository.findByOrderDateBetween(start, end);

            if (orders.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found in the given date range");
            }
            return orders;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date and end date are required");
    }

    @Override
    public Double getTotalAmountSpentByCustomer(Long customerId) {
        return orderRepository.getTotalAmountSpentByCustomer(customerId);
    }
}

