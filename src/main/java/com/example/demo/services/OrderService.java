package com.example.demo.services;



import java.util.List;

import com.example.demo.models.Orders;

public interface OrderService {

    List<Orders> placeOrders(List<Orders> orders, Long userId);
  
    Orders updateOrderStatus(Long id, String status);

    List<Orders> getAllOrders();

    List<Orders> getOrdersByCustomer(Long customerId);

    List<Orders> getOrdersByDateRange(String startDate, String endDate);

    Double getTotalAmountSpentByCustomer(Long customerId);
}
