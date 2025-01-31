package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Customer;

public interface CustomerService {

	List<Customer> getAllCustomers();

	Customer createCustomer(Customer customer);

	Customer updateCustomer(Long id, Customer customer);

	void deleteCustomer(Long id);

	Customer getCustomerById(Long id);

}
