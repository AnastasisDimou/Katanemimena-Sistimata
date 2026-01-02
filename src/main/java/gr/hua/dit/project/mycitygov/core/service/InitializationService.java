package gr.hua.dit.project.mycitygov.core.service;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;

/**
 * Initializes application data.
 */
@Service
public class InitializationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AtomicBoolean initialized;

	public InitializationService(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
		if (userRepository == null)
			throw new NullPointerException();
		if (passwordEncoder == null)
			throw new NullPointerException();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.initialized = new AtomicBoolean(false);
	}

	@PostConstruct
	public void populateDatabaseWithInitialData() {
		final boolean alreadyInitialized = this.initialized.getAndSet(true);
		if (alreadyInitialized) {
			LOGGER.warn("Database initialization skipped: initial data has already been populated.");
			return;
		}
		LOGGER.info("Starting database initialization with initial data...");
		userRepository.deleteAll();

		final String hash = passwordEncoder.encode("password");

		final List<User> users = List.of(
				new User(
						null,
						"123456789", // afm
						"11111111111", // amka
						"Giorgos", // firstName
						"Papadopoulos", // lastName
						"citizen@example.com",
						"6912345678", // phoneNumber
						hash,
						UserType.CITIZEN),
				new User(
						null,
						"987654321",
						"22222222222",
						"Maria",
						"Ioannou",
						"employee@example.com",
						"6987654321",
						hash,
						UserType.EMPLOYEE),
				new User(
						null,
						"555555555",
						"33333333333",
						"Admin",
						"User",
						"admin@example.com",
						"6999999999",
						hash,
						UserType.ADMIN));

		userRepository.saveAll(users);

		LOGGER.info("=== Users in database ===");
		userRepository.findAll().forEach(user -> LOGGER.info("User: {}", user));

		userRepository.findByEmail("admin@example.com")
				.ifPresent(user -> LOGGER.info("Found admin: {}", user));
		LOGGER.info("Database initialization completed successfully.");
	}
}
