package com.agendamentotenis.tennis_scheduling;

import com.agendamentotenis.model.User;
import com.agendamentotenis.repository.UserRepository;
import com.agendamentotenis.service.UserService;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = new User("Test User", "test@example.com", "password123");
	}

	@Test
	void createUser_Success() {
		// Given
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		// When
		User createdUser = userService.createUser(testUser);

		// Then
		assertNotNull(createdUser);
		assertEquals(testUser.getName(), createdUser.getName());
		assertEquals(testUser.getEmail(), createdUser.getEmail());
		verify(passwordEncoder).encode("password123"); // Fix: verify with original password
		verify(userRepository).save(any(User.class));
	}

	@Test
	void createUser_DuplicateEmail() {
		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		assertThrows(RuntimeException.class, () -> {
			userService.createUser(testUser);
		});

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void createUser_WithNullEmail() {
		testUser.setEmail(null);

		assertThrows(RuntimeException.class, () -> {
			userService.createUser(testUser);
		});

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void findAllUsers_Success() {
		// Given
		List<User> userList = Arrays.asList(
				new User("User1", "user1@example.com", "pass1"),
				new User("User2", "user2@example.com", "pass2"));
		when(userRepository.findAll()).thenReturn(userList);

		// When
		List<User> foundUsers = userService.findAllUsers();

		// Then
		assertNotNull(foundUsers);
		assertEquals(2, foundUsers.size());
		assertEquals("User1", foundUsers.get(0).getName());
		assertEquals("User2", foundUsers.get(1).getName());
		verify(userRepository).findAll();
	}

	@Test
	void findByEmail_Success() {
		// Given
		String email = "test@example.com";
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

		// When
		Optional<User> foundUser = userService.findByEmail(email);

		// Then
		assertTrue(foundUser.isPresent());
		assertEquals(testUser.getEmail(), foundUser.get().getEmail());
		assertEquals(testUser.getName(), foundUser.get().getName());
		verify(userRepository).findByEmail(email);
	}

	@Test
	void findByEmail_NotFound() {
		// Given
		String email = "nonexistent@example.com";
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		// When
		Optional<User> foundUser = userService.findByEmail(email);

		// Then
		assertTrue(foundUser.isEmpty());
		verify(userRepository).findByEmail(email);
	}

	@Test
	void updateUser_Success() {
		// Given
		String userId = "testId";
		User updatedDetails = new User("Updated Name", "test@example.com", "password123");
		when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
		when(userRepository.save(any(User.class))).thenReturn(updatedDetails);

		// When
		User updatedUser = userService.updateUser(userId, updatedDetails);

		// Then
		assertNotNull(updatedUser);
		assertEquals(updatedDetails.getName(), updatedUser.getName());
		verify(userRepository).findById(userId);
		verify(userRepository).save(any(User.class));
	}

	@Test
	void updateUser_UserNotFound() {
		// Given
		String userId = "nonexistentId";
		User updatedDetails = new User("Updated Name", "test@example.com", "password123");
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// Then
		assertThrows(RuntimeException.class, () -> {
			userService.updateUser(userId, updatedDetails);
		});
		verify(userRepository).findById(userId);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void deleteUser_Success() {
		// Given
		String userId = "testId";
		doNothing().when(userRepository).deleteById(userId);

		// When
		userService.deleteUser(userId);

		// Then
		verify(userRepository).deleteById(userId);
	}

}