package gr.hua.dit.project.mycitygov;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;

@SpringBootApplication
public class MycitygovApplication {

	public static void main(String[] args) {
		SpringApplication.run(MycitygovApplication.class, args);
	}

	@Bean
	CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			userRepository.deleteAll();

			String hash = passwordEncoder.encode("password");

			User citizen = new User(
					null,
					"123456789", // afm
					"11111111111", // amka
					"Giorgos", // firstName
					"Papadopoulos", // lastName
					"citizen@example.com",
					"6912345678", // phoneNumber
					hash,
					UserType.CITIZEN);

			User employee = new User(
					null,
					"987654321",
					"22222222222",
					"Maria",
					"Ioannou",
					"employee@example.com",
					"6987654321",
					hash,
					UserType.EMPLOYEE);

			User admin = new User(
					null,
					"555555555",
					"33333333333",
					"Admin",
					"User",
					"admin@example.com",
					"6999999999",
					hash,
					UserType.ADMIN);

			userRepository.save(citizen);
			userRepository.save(employee);
			userRepository.save(admin);

			System.out.println("=== Users in database ===");
			userRepository.findAll().forEach(System.out::println);

			System.out.println("=== Find by email (admin@example.com) ===");
			userRepository.findByEmail("admin@example.com")
					.ifPresent(user -> System.out.println("Found admin: " + user));
		};
	}
}
