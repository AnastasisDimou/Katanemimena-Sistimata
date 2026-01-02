package gr.hua.dit.project.mycitygov.core.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.dit.project.mycitygov.core.model.Request;
import gr.hua.dit.project.mycitygov.core.model.RequestStatus;
import gr.hua.dit.project.mycitygov.core.model.RequestType;
import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;
import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.RequestRepository;
import gr.hua.dit.project.mycitygov.core.repository.RequestTypeRepository;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import gr.hua.dit.project.mycitygov.core.security.CurrentUser;
import gr.hua.dit.project.mycitygov.core.security.CurrentUserProvider;
import gr.hua.dit.project.mycitygov.core.service.model.DepartmentSummary;
import gr.hua.dit.project.mycitygov.core.service.model.TicketDetailsView;
import gr.hua.dit.project.mycitygov.core.service.model.TicketListItem;
import gr.hua.dit.project.mycitygov.core.service.model.UserSummary;
import gr.hua.dit.project.mycitygov.web.ui.model.OpenTicketForm;
import gr.hua.dit.project.mycitygov.web.ui.model.UpdateTicketStatusForm;

@Service
public class RequestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);
	private static final String PROTOCOL_PREFIX = "REQ-";

	private final RequestRepository requestRepository;
	private final RequestTypeRepository requestTypeRepository;
	private final UserRepository userRepository;
	private final CurrentUserProvider currentUserProvider;

	public RequestService(
			final RequestRepository requestRepository,
			final RequestTypeRepository requestTypeRepository,
			final UserRepository userRepository,
			final CurrentUserProvider currentUserProvider) {
		this.requestRepository = Objects.requireNonNull(requestRepository);
		this.requestTypeRepository = Objects.requireNonNull(requestTypeRepository);
		this.userRepository = Objects.requireNonNull(userRepository);
		this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
	}

	@Transactional(readOnly = true)
	public List<TicketListItem> getCitizenTickets() {
		final User citizen = requireAuthenticatedUser(UserType.CITIZEN);
		return this.requestRepository.findByCitizenOrderBySubmissionDateDesc(citizen)
				.stream()
				.map(this::toListItem)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<TicketListItem> getDepartmentTickets() {
		final User employee = requireAuthenticatedUser(UserType.EMPLOYEE);
		final ServiceDepartment dept = employee.getServiceDepartment();
		if (dept == null) {
			throw new SecurityException("Employee is not linked to a service department");
		}
		return this.requestRepository.findByRequestType_ServiceDepartmentOrderBySubmissionDateDesc(dept)
				.stream()
				.map(this::toListItem)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<TicketListItem> getAllTickets() {
		requireAuthenticatedUser(UserType.ADMIN);
		return this.requestRepository.findAllByOrderBySubmissionDateDesc()
				.stream()
				.map(this::toListItem)
				.toList();
	}

	@Transactional(readOnly = true)
	public TicketDetailsView getCitizenTicket(final Long requestId) {
		if (requestId == null)
			throw new NullPointerException("requestId cannot be null");
		final User citizen = requireAuthenticatedUser(UserType.CITIZEN);

		final Request req = this.requestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("Request not found"));

		if (!req.getCitizen().getId().equals(citizen.getId())) {
			throw new SecurityException("Citizen cannot access this request");
		}

		return toDetailsView(req);
	}

	@Transactional(readOnly = true)
	public TicketDetailsView getEmployeeTicket(final Long requestId) {
		if (requestId == null)
			throw new NullPointerException("requestId cannot be null");
		final CurrentUser current = this.currentUserProvider.requireCurrentUser();

		final Request req = this.requestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("Request not found"));

		if (current.type() == UserType.ADMIN) {
			return toDetailsView(req);
		}

		if (current.type() != UserType.EMPLOYEE) {
			throw new SecurityException("Employee role required");
		}

		final User employee = this.userRepository.findByEmailIgnoreCase(current.email())
				.orElseThrow(() -> new IllegalStateException("Authenticated employee not found"));
		final ServiceDepartment dept = employee.getServiceDepartment();
		if (dept == null) {
			throw new SecurityException("Employee is not linked to a service department");
		}
		if (!req.getRequestType().getServiceDepartment().getId().equals(dept.getId())) {
			throw new SecurityException("Employee cannot access this request");
		}

		return toDetailsView(req);
	}

	@Transactional
	public TicketDetailsView createCitizenTicket(final OpenTicketForm form) {
		if (form == null)
			throw new NullPointerException("form cannot be null");

		final User citizen = requireAuthenticatedUser(UserType.CITIZEN);

		final RequestType requestType = this.requestTypeRepository.findById(form.requestTypeId())
				.orElseThrow(() -> new IllegalArgumentException("Request type not found"));

		final LocalDateTime submittedAt = LocalDateTime.now().withSecond(0).withNano(0);
		final Integer maxDays = requestType.getMaxProcessingDays();
		final LocalDateTime dueAt = submittedAt.plusDays(maxDays != null ? maxDays : 5);

		Request req = new Request();
		req.setProtocolNumber(null); // assigned after first save
		req.setSubject(form.subject());
		req.setCitizen(citizen);
		req.setRequestType(requestType);
		req.setStatus(RequestStatus.SUBMITTED);
		req.setSubmissionDate(submittedAt);
		req.setDueDate(dueAt);
		req.setCompletionDate(null);
		req.setAssignedEmployee(null);
		req.setDescription(form.content());
		req.setAttachmentUrl(form.attachmentUrl());
		req.setEmployeeComments(null);

		req = this.requestRepository.save(req);
		req.setProtocolNumber(generateProtocol(req.getId()));
		req = this.requestRepository.save(req);

		LOGGER.info("Citizen {} created request {}", citizen.getEmail(), req.getProtocolNumber());
		return toDetailsView(req);
	}

	@Transactional
	public TicketDetailsView assignToCurrentEmployee(final Long requestId) {
		if (requestId == null)
			throw new NullPointerException("requestId cannot be null");

		final User employee = requireAuthenticatedUser(UserType.EMPLOYEE);
		final ServiceDepartment dept = employee.getServiceDepartment();
		if (dept == null)
			throw new SecurityException("Employee is not linked to a service department");

		final Request req = this.requestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("Request not found"));

		if (!req.getRequestType().getServiceDepartment().getId().equals(dept.getId())) {
			throw new SecurityException("Employee cannot access this request");
		}

		if (req.getAssignedEmployee() != null && !req.getAssignedEmployee().getId().equals(employee.getId())) {
			throw new IllegalStateException("Request already assigned to another employee");
		}

		req.setAssignedEmployee(employee);
		req.setStatus(RequestStatus.IN_PROGRESS);
		final Request saved = this.requestRepository.save(req);
		return toDetailsView(saved);
	}

	@Transactional
	public TicketDetailsView updateStatus(final Long requestId, final UpdateTicketStatusForm form) {
		if (requestId == null)
			throw new NullPointerException("requestId cannot be null");
		if (form == null)
			throw new NullPointerException("form cannot be null");

		final User employee = requireAuthenticatedUser(UserType.EMPLOYEE);
		final Request req = this.requestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("Request not found"));

		if (req.getAssignedEmployee() == null || !req.getAssignedEmployee().getId().equals(employee.getId())) {
			throw new SecurityException("Request is not assigned to the current employee");
		}

		final RequestStatus newStatus = form.newStatus();
		if (newStatus == null) {
			throw new IllegalArgumentException("newStatus cannot be null");
		}

		// Basic rule: do not allow moving back from COMPLETED/REJECTED
		if (req.getStatus() == RequestStatus.COMPLETED || req.getStatus() == RequestStatus.REJECTED) {
			throw new IllegalStateException("Completed/rejected requests cannot change status");
		}

		req.setStatus(newStatus);
		if (newStatus == RequestStatus.COMPLETED) {
			req.setCompletionDate(LocalDateTime.now().withSecond(0).withNano(0));
		}
		req.setEmployeeComments(form.comments());

		final Request saved = this.requestRepository.save(req);
		return toDetailsView(saved);
	}

	private TicketListItem toListItem(final Request req) {
		final DepartmentSummary dept = new DepartmentSummary(
				req.getRequestType().getServiceDepartment().getId(),
				req.getRequestType().getServiceDepartment().getCode(),
				req.getRequestType().getServiceDepartment().getName());
		return new TicketListItem(
				req.getId(),
				req.getProtocolNumber(),
				req.getStatus(),
				req.getSubject(),
				dept,
				req.getSubmissionDate(),
				req.getDueDate());
	}

	private TicketDetailsView toDetailsView(final Request req) {
		final DepartmentSummary dept = new DepartmentSummary(
				req.getRequestType().getServiceDepartment().getId(),
				req.getRequestType().getServiceDepartment().getCode(),
				req.getRequestType().getServiceDepartment().getName());

		final User assigned = req.getAssignedEmployee();
		final UserSummary assignedSummary = assigned == null ? null
				: new UserSummary(
						assigned.getId(),
						assigned.getFirstName() + " " + assigned.getLastName(),
						assigned.getEmail());

		return new TicketDetailsView(
				req.getId(),
				req.getProtocolNumber(),
				req.getStatus(),
				req.getSubject(),
				dept,
				req.getSubmissionDate(),
				req.getDueDate(),
				req.getCompletionDate(),
				req.getDescription(),
				req.getAttachmentUrl(),
				assignedSummary,
				req.getEmployeeComments());
	}

	private User requireAuthenticatedUser(final UserType expectedType) {
		final CurrentUser current = this.currentUserProvider.requireCurrentUser();
		if (current.type() != expectedType) {
			throw new SecurityException("Expected role " + expectedType);
		}
		return this.userRepository.findByEmailIgnoreCase(current.email())
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
	}

	private String generateProtocol(final Long id) {
		if (id == null) {
			throw new IllegalStateException("Cannot generate protocol without id");
		}
		return PROTOCOL_PREFIX + String.format("%06d", id);
	}
}
