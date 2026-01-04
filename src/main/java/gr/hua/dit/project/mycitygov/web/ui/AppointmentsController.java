package gr.hua.dit.project.mycitygov.web.ui;

import gr.hua.dit.project.mycitygov.core.repository.ServiceDepartmentRepository;
import gr.hua.dit.project.mycitygov.core.service.AppointmentService;
import gr.hua.dit.project.mycitygov.core.service.model.AppointmentDetailsView;
import gr.hua.dit.project.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.project.mycitygov.web.ui.model.AppointmentCreateForm;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AppointmentsController {

   private final AppointmentService appointmentService;
   private final ServiceDepartmentRepository serviceDepartmentRepository;

   public AppointmentsController(
         final AppointmentService appointmentService,
         final ServiceDepartmentRepository serviceDepartmentRepository) {
      this.appointmentService = appointmentService;
      this.serviceDepartmentRepository = serviceDepartmentRepository;
   }

   @GetMapping("/appointments")
   public String showAppointments(final Model model) {
      final var appointments = this.appointmentService.listForCurrentUser();
      model.addAttribute("appointments", appointments);
      return "appointments";
   }

   @GetMapping("/appointments/{id}")
   public String showAppointment(@PathVariable("id") final Long id, final Model model) {
      final AppointmentDetailsView appointment = this.appointmentService.getForCurrentUser(id);
      model.addAttribute("appointment", appointment);
      return "appointment";
   }

   @GetMapping("/appointments/new")
   public String newAppointmentForm(final Model model) {
      model.addAttribute("form", new AppointmentCreateForm(null, null));
      model.addAttribute("departments", this.serviceDepartmentRepository.findAll());
      return "appointment_new";
   }

   @PostMapping("/appointments/new")
   public String handleNewAppointment(
         @ModelAttribute("form") @Valid final AppointmentCreateForm form,
         final BindingResult bindingResult,
         final Model model) {
      if (bindingResult.hasErrors()) {
         model.addAttribute("departments", this.serviceDepartmentRepository.findAll());
         return "appointment_new";
      }
      final AppointmentView created = this.appointmentService.createAppointment(form);
      return "redirect:/appointments?created=" + created.protocolNumber();
   }
}
