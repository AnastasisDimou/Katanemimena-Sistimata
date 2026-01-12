package gr.hua.dit.project.mycitygov.core.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.dit.project.mycitygov.core.model.Appointment;
import gr.hua.dit.project.mycitygov.core.model.AppointmentStatus;
import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;
import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.AppointmentRepository;
import gr.hua.dit.project.mycitygov.core.repository.ServiceDepartmentRepository;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import gr.hua.dit.project.mycitygov.core.security.CurrentUser;
import gr.hua.dit.project.mycitygov.core.security.CurrentUserProvider;
import gr.hua.dit.project.mycitygov.core.service.model.AppointmentDetailsView;
import gr.hua.dit.project.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.project.mycitygov.core.service.model.DepartmentSummary;
import gr.hua.dit.project.mycitygov.core.service.model.UserSummary;
import gr.hua.dit.project.mycitygov.web.ui.model.AppointmentCreateForm;
import gr.hua.dit.project.mycitygov.web.ui.model.UpdateAppointmentStatusForm;

@Service
public class AppointmentService {

   private static final String PROTOCOL_PREFIX = "APP-";

   private final AppointmentRepository appointmentRepository;
   private final UserRepository userRepository;
   private final ServiceDepartmentRepository serviceDepartmentRepository;
   private final CurrentUserProvider currentUserProvider;

   public AppointmentService(
         final AppointmentRepository appointmentRepository,
         final UserRepository userRepository,
         final ServiceDepartmentRepository serviceDepartmentRepository,
         final CurrentUserProvider currentUserProvider) {
      this.appointmentRepository = appointmentRepository;
      this.userRepository = userRepository;
      this.serviceDepartmentRepository = serviceDepartmentRepository;
      this.currentUserProvider = currentUserProvider;
   }

   @Transactional(readOnly = true)
   public List<AppointmentView> listForCurrentUser() {
      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      final User user = this.userRepository.findByEmailIgnoreCase(me.email())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

      final List<Appointment> appointments = switch (me.type()) {
         case CITIZEN -> this.appointmentRepository.findByCitizen(user);
         case EMPLOYEE -> this.appointmentRepository.findByServiceDepartment(user.getServiceDepartment());
         case ADMIN -> this.appointmentRepository.findAll();
      };

      return appointments.stream().map(this::toView).toList();
   }

   @Transactional(readOnly = true)
   public AppointmentDetailsView getForCurrentUser(final Long id) {
      if (id == null) {
         throw new IllegalArgumentException("id cannot be null");
      }

      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      final User user = this.userRepository.findByEmailIgnoreCase(me.email())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

      final Appointment appt = this.appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

      switch (me.type()) {
         case CITIZEN -> {
            if (!appt.getCitizen().getId().equals(user.getId())) {
               throw new SecurityException("You are not allowed to view this appointment");
            }
         }
         case EMPLOYEE -> {
            final ServiceDepartment dept = user.getServiceDepartment();
            if (dept == null || !dept.getId().equals(appt.getServiceDepartment().getId())) {
               throw new SecurityException("You are not allowed to view this appointment");
            }
         }
         case ADMIN -> {
            // Admin can view any appointment
         }
      }

      return toDetails(appt);
   }

   @Transactional
   public AppointmentView createAppointment(final AppointmentCreateForm form) {
      if (form == null)
         throw new NullPointerException("form cannot be null");

      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      if (me.type() != UserType.CITIZEN) {
         throw new SecurityException("Only citizens can create appointments");
      }

      final User citizen = this.userRepository.findByEmailIgnoreCase(me.email())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

      final ServiceDepartment dept = this.serviceDepartmentRepository.findById(form.departmentId())
            .orElseThrow(() -> new IllegalArgumentException("Department not found"));

      LocalDateTime when = form.appointmentDateTime();
      if (when == null) {
         throw new IllegalArgumentException("appointmentDateTime cannot be null");
      }

      Appointment appt = new Appointment();
      appt.setProtocolNumber("APP-TMP-" + UUID.randomUUID());
      appt.setCitizen(citizen);
      appt.setServiceDepartment(dept);
      appt.setAppointmentDateTime(when);
      appt.setStatus(AppointmentStatus.NOT_COMPLETED);

      appt = this.appointmentRepository.save(appt);
      appt.setProtocolNumber(generateProtocol(appt.getId()));
      appt = this.appointmentRepository.save(appt);

      return toView(appt);
   }

   @Transactional
   public AppointmentDetailsView updateStatus(final Long id, final UpdateAppointmentStatusForm form) {
      if (id == null) {
         throw new IllegalArgumentException("id cannot be null");
      }
      if (form == null) {
         throw new IllegalArgumentException("form cannot be null");
      }

      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      if (me.type() != UserType.EMPLOYEE && me.type() != UserType.ADMIN) {
         throw new SecurityException("Employee or admin role required");
      }

      final Appointment appt = this.appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

      if (me.type() == UserType.EMPLOYEE) {
         final User employee = this.userRepository.findByEmailIgnoreCase(me.email())
               .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
         final ServiceDepartment dept = employee.getServiceDepartment();
         if (dept == null || !dept.getId().equals(appt.getServiceDepartment().getId())) {
            throw new SecurityException("You are not allowed to update this appointment");
         }
      }

      final AppointmentStatus newStatus = form.status();
      if (newStatus == null) {
         throw new IllegalArgumentException("status cannot be null");
      }

      appt.setStatus(newStatus);
      final Appointment saved = this.appointmentRepository.save(appt);
      return toDetails(saved);
   }

   private AppointmentView toView(final Appointment appt) {
      final DepartmentSummary dept = new DepartmentSummary(
            appt.getServiceDepartment().getId(),
            appt.getServiceDepartment().getCode(),
            appt.getServiceDepartment().getName());
      return new AppointmentView(
            appt.getId(),
            appt.getProtocolNumber(),
            dept,
            appt.getAppointmentDateTime(),
            resolveStatus(appt));
   }

   private AppointmentDetailsView toDetails(final Appointment appt) {
      final DepartmentSummary dept = new DepartmentSummary(
            appt.getServiceDepartment().getId(),
            appt.getServiceDepartment().getCode(),
            appt.getServiceDepartment().getName());
      final User citizen = appt.getCitizen();
      final UserSummary citizenSummary = new UserSummary(
            citizen.getId(),
            citizen.getFirstName() + " " + citizen.getLastName(),
            citizen.getEmail());
      return new AppointmentDetailsView(
            appt.getId(),
            appt.getProtocolNumber(),
            dept,
            appt.getServiceDepartment().getDescription(),
            appt.getAppointmentDateTime(),
            resolveStatus(appt),
            citizenSummary,
            citizen.getPhoneNumber());
   }

   private AppointmentStatus resolveStatus(final Appointment appt) {
      if (appt.getStatus() != null) {
         return appt.getStatus();
      }
      final LocalDateTime when = appt.getAppointmentDateTime();
      if (when == null) {
         return AppointmentStatus.NOT_COMPLETED;
      }
      return when.isBefore(LocalDateTime.now())
            ? AppointmentStatus.COMPLETED
            : AppointmentStatus.NOT_COMPLETED;
   }

   private String generateProtocol(final Long id) {
      if (id == null) {
         throw new IllegalStateException("Cannot generate protocol without id");
      }
      return PROTOCOL_PREFIX + String.format("%06d", id);
   }
}
