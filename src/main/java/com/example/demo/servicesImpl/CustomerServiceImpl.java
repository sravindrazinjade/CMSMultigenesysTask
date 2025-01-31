package com.example.demo.servicesImpl;

import com.example.demo.models.Customer;
import com.example.demo.models.User;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Customer createCustomer(Customer customer) {
		Optional<User> optional = userRepository.findByUsername(customer.getPhoneNumber());
		if (optional.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in system");
		}
         
		if (customerRepository.findByUserId(optional.get().getId()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer already exists");
		}

		User user = optional.get();
		customer.setUserId(user.getId());
		customer.setCreatedAt(LocalDate.now());
		return customerRepository.save(customer);
	}

	@Override
	public Customer updateCustomer(Long id, Customer customer) {
		Customer existingCustomer = customerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		existingCustomer.setName(customer.getName());
		existingCustomer.setEmail(customer.getEmail());
		existingCustomer.setPhoneNumber(customer.getPhoneNumber());
		return customerRepository.save(existingCustomer);
	}

	@Override
	public void deleteCustomer(Long id) {
		customerRepository.deleteById(id);
	}

	@Override
	public Customer getCustomerById(Long id) {
		return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
	}

	@Override
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}
}
