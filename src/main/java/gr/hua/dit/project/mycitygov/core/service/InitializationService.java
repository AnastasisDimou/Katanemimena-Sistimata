package gr.hua.dit.project.mycitygov.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import gr.hua.dit.project.mycitygov.core.model.Appointment;
import gr.hua.dit.project.mycitygov.core.model.Request;
import gr.hua.dit.project.mycitygov.core.model.RequestStatus;
import gr.hua.dit.project.mycitygov.core.model.RequestType;
import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;
import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.AppointmentRepository;
import gr.hua.dit.project.mycitygov.core.repository.RequestRepository;
import gr.hua.dit.project.mycitygov.core.repository.RequestTypeRepository;
import gr.hua.dit.project.mycitygov.core.repository.ServiceDepartmentRepository;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;

@Service
public class InitializationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

	private final UserRepository userRepository;
	private final ServiceDepartmentRepository serviceDepartmentRepository;
	private final RequestTypeRepository requestTypeRepository;
	private final RequestRepository requestRepository;
	private final AppointmentRepository appointmentRepository;
	private final PasswordEncoder passwordEncoder;

	private final AtomicBoolean initialized;

	public InitializationService(
			final UserRepository userRepository,
			final ServiceDepartmentRepository serviceDepartmentRepository,
			final RequestTypeRepository requestTypeRepository,
			final RequestRepository requestRepository,
			final AppointmentRepository appointmentRepository,
			final PasswordEncoder passwordEncoder) {

		if (userRepository == null)
			throw new NullPointerException();
		if (serviceDepartmentRepository == null)
			throw new NullPointerException();
		if (requestTypeRepository == null)
			throw new NullPointerException();
		if (requestRepository == null)
			throw new NullPointerException();
		if (appointmentRepository == null)
			throw new NullPointerException();
		if (passwordEncoder == null)
			throw new NullPointerException();

		this.userRepository = userRepository;
		this.serviceDepartmentRepository = serviceDepartmentRepository;
		this.requestTypeRepository = requestTypeRepository;
		this.requestRepository = requestRepository;
		this.appointmentRepository = appointmentRepository;
		this.passwordEncoder = passwordEncoder;

		this.initialized = new AtomicBoolean(false);
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	@SuppressWarnings("null")
	public void populateDatabaseWithInitialData() {
		final boolean alreadyInitialized = this.initialized.getAndSet(true);
		if (alreadyInitialized) {
			LOGGER.warn("Database initialization skipped: initial data has already been populated.");
			return;
		}

		LOGGER.info("Starting database initialization with initial data...");

		// IMPORTANT: delete in FK-safe order
		appointmentRepository.deleteAll();
		requestRepository.deleteAll();
		requestTypeRepository.deleteAll();
		userRepository.deleteAll();
		serviceDepartmentRepository.deleteAll();

		// 1) Departments
		final ServiceDepartment kep = new ServiceDepartment(null, "KEP", "ΚΕΠ", "Κέντρο Εξυπηρέτησης Πολιτών");
		final ServiceDepartment tech = new ServiceDepartment(null, "TECH", "Τεχνική Υπηρεσία", "Τεχνικά αιτήματα δήμου");
		final ServiceDepartment clean = new ServiceDepartment(null, "CLEAN", "Καθαριότητα", "Καθαριότητα & αποκομιδές");
		final ServiceDepartment fin = new ServiceDepartment(null, "FIN", "Οικονομική Υπηρεσία",
				"Οφειλές, βεβαιώσεις, ρυθμίσεις");
		final ServiceDepartment soc = new ServiceDepartment(null, "SOC", "Κοινωνική Υπηρεσία",
				"Κοινωνικές παροχές & υποστήριξη");

		final List<ServiceDepartment> departments = List.of(kep, tech, clean, fin, soc);
		serviceDepartmentRepository.saveAll(departments);

		// 2) Request types (each mapped to a department)
		final RequestType rtKep1 = new RequestType(null, "RT-KEP-001", "Βεβαίωση Μόνιμης Κατοικίας",
				"Έκδοση βεβαίωσης μόνιμης κατοικίας.", true, 5, kep);
		final RequestType rtKep2 = new RequestType(null, "RT-KEP-002", "Βεβαίωση Οικογενειακής Κατάστασης",
				"Έκδοση βεβαίωσης οικογενειακής κατάστασης.", true, 5, kep);

		final RequestType rtTech1 = new RequestType(null, "RT-TECH-001", "Επισκευή Δημοτικού Φωτισμού",
				"Αίτημα επισκευής/αντικατάστασης φωτιστικού.", true, 10, tech);
		final RequestType rtTech2 = new RequestType(null, "RT-TECH-002", "Αποκατάσταση Οδοστρώματος",
				"Αίτημα επιδιόρθωσης λακκούβας/οδοστρώματος.", true, 15, tech);

		final RequestType rtClean1 = new RequestType(null, "RT-CLEAN-001", "Αποκομιδή Ογκωδών",
				"Αίτημα αποκομιδής ογκωδών αντικειμένων.", true, 7, clean);
		final RequestType rtClean2 = new RequestType(null, "RT-CLEAN-002", "Καθαριότητα Κοινόχρηστου Χώρου",
				"Αίτημα καθαριότητας σε κοινόχρηστο χώρο.", true, 7, clean);

		final RequestType rtFin1 = new RequestType(null, "RT-FIN-001", "Βεβαίωση Μη Οφειλής",
				"Έκδοση βεβαίωσης μη οφειλής προς τον δήμο.", true, 7, fin);
		final RequestType rtFin2 = new RequestType(null, "RT-FIN-002", "Ρύθμιση Οφειλών",
				"Αίτημα ρύθμισης/διακανονισμού οφειλών.", true, 20, fin);

		final RequestType rtSoc1 = new RequestType(null, "RT-SOC-001", "Αίτημα Κοινωνικής Παροχής",
				"Αίτημα για κοινωνική παροχή/ενίσχυση.", true, 30, soc);
		final RequestType rtSoc2 = new RequestType(null, "RT-SOC-002", "Βοήθεια στο Σπίτι",
				"Αίτημα ένταξης στο πρόγραμμα «Βοήθεια στο Σπίτι».", true, 14, soc);

		final List<RequestType> requestTypes = List.of(
				rtKep1, rtKep2,
				rtTech1, rtTech2,
				rtClean1, rtClean2,
				rtFin1, rtFin2,
				rtSoc1, rtSoc2);

		requestTypeRepository.saveAll(requestTypes);

		// 3) Users (employees linked to departments)
		final String hash = passwordEncoder.encode("password");

		final User citizen = new User(null, "123456789", "11111111111", "Giorgos", "Papadopoulos",
				"citizen@example.com", "6912345678", hash, UserType.CITIZEN);

		final User employeeKep = new User(null, "987654321", "22222222222", "Maria", "Ioannou",
				"employee@example.com", "6987654321", hash, UserType.EMPLOYEE);
		employeeKep.setServiceDepartment(kep);

		final User employeeTech = new User(null, "111222333", "33333333333", "Nikos", "Georgiou",
				"tech.employee@example.com", "6900000001", hash, UserType.EMPLOYEE);
		employeeTech.setServiceDepartment(tech);

		final User employeeClean = new User(null, "444555666", "44444444444", "Eleni", "Kosta",
				"clean.employee@example.com", "6900000002", hash, UserType.EMPLOYEE);
		employeeClean.setServiceDepartment(clean);

		final User admin = new User(null, "555555555", "99999999999", "Admin", "User",
				"admin@example.com", "6999999999", hash, UserType.ADMIN);

		final List<User> users = List.of(citizen, employeeKep, employeeTech, employeeClean, admin);
		userRepository.saveAll(users);

		// 4) Optional demo Requests + Appointments (useful for testing screens later)
		final LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

		final Request r1 = new Request(
				"REQ-000001",
				"Βεβαίωση μόνιμης κατοικίας",
				citizen,
				rtKep1,
				RequestStatus.SUBMITTED,
				now.minusDays(1),
				now.minusDays(1).plusDays(rtKep1.getMaxProcessingDays()),
				null,
				employeeKep,
				null,
				null,
				"Παρακαλώ για έκδοση βεβαίωσης μόνιμης κατοικίας.");

		final Request r2 = new Request(
				"REQ-000002",
				"Επισκευή δημοτικού φωτισμού",
				citizen,
				rtTech1,
				RequestStatus.IN_PROGRESS,
				now.minusDays(3),
				now.minusDays(3).plusDays(rtTech1.getMaxProcessingDays()),
				null,
				employeeTech,
				null,
				null,
				"Ο δημοτικός φωτισμός στην οδό X δεν λειτουργεί.");

		final Request r3 = new Request(
				"REQ-000003",
				"Αποκομιδή ογκωδών",
				citizen,
				rtClean1,
				RequestStatus.COMPLETED,
				now.minusDays(10),
				now.minusDays(10).plusDays(rtClean1.getMaxProcessingDays()),
				now.minusDays(6),
				employeeClean,
				null,
				null,
				"Αποκομιδή ογκωδών από την οδό Y.");

		final List<Request> requests = List.of(r1, r2, r3);
		requestRepository.saveAll(requests);

		final Appointment a1 = new Appointment(null, "APP-000001", citizen, kep,
				now.plusDays(2).withHour(10).withMinute(0));
		final Appointment a2 = new Appointment(null, "APP-000002", citizen, tech,
				now.plusDays(4).withHour(12).withMinute(30));

		final List<Appointment> appointments = List.of(a1, a2);
		appointmentRepository.saveAll(appointments);

		LOGGER.info("Database initialization completed.");
		LOGGER.info("Departments: {}", serviceDepartmentRepository.count());
		LOGGER.info("RequestTypes: {}", requestTypeRepository.count());
		LOGGER.info("Users: {}", userRepository.count());
		LOGGER.info("Requests: {}", requestRepository.count());
		LOGGER.info("Appointments: {}", appointmentRepository.count());
	}
}
