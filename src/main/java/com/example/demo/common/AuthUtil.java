package com.example.demo.common;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import java.util.Optional;

@Component
public class AuthUtil {

	private static UserRepository userRepository;

	@Autowired
	public AuthUtil(UserRepository userRepository) {
		AuthUtil.userRepository = userRepository;
	}

	public static String getCurrentUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		} else {
			return principal.toString();
		}
	}

	public static Long getCurrentUserId() {
		String username = getCurrentUsername();
		Optional<User> user = userRepository.findByUsername(username);
		return user.map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
	}
}
