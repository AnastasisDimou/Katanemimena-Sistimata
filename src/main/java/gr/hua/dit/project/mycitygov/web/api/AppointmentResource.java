package gr.hua.dit.project.mycitygov.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import gr.hua.dit.project.mycitygov.core.model.Appointment;
import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.repository.AppointmentRepository;
import gr.hua.dit.project.mycitygov.core.repository.UserRepository;
import gr.hua.dit.project.mycitygov.core.security.CurrentUser;
import gr.hua.dit.project.mycitygov.core.security.CurrentUserProvider;
import gr.hua.dit.project.mycitygov.core.service.AppointmentService;
import gr.hua.dit.project.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.project.mycitygov.core.service.model.DepartmentSummary;
import gr.hua.dit.project.mycitygov.web.ui.model.AppointmentCreateForm;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Appointments")
public class AppointmentResource {

   private final AppointmentRepository appointmentRepository;
   private final UserRepository userRepository;
   private final CurrentUserProvider currentUserProvider;
   private final AppointmentService appointmentService;

   public AppointmentResource(
         final AppointmentRepository appointmentRepository,
         final UserRepository userRepository,
         final CurrentUserProvider currentUserProvider,
         final AppointmentService appointmentService) {
      this.appointmentRepository = appointmentRepository;
      this.userRepository = userRepository;
      this.currentUserProvider = currentUserProvider;
      this.appointmentService = appointmentService;
   }

   @Operation(summary = "List appointments for current user")
   @GetMapping("")
   public List<AppointmentView> list() {
      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      final User user = this.userRepository.findByEmailIgnoreCase(me.email())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

      final List<Appointment> appointments = switch (me.type()) {
         case CITIZEN -> this.appointmentRepository.findByCitizen(user);
         case EMPLOYEE -> this.appointmentRepository.findByServiceDepartment(user.getServiceDepartment());
         case ADMIN -> this.appointmentRepository.findAll();
      };

      return appointments.stream()
            .map(this::toView)
            .toList();
   }

   @Operation(summary = "Create appointment (citizen)")
   @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
   public AppointmentView create(@RequestBody final AppointmentCreateForm form) {
      return this.appointmentService.createAppointment(form);
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
            appt.getAppointmentDateTime());
   }
}
