package gr.hua.dit.project.mycitygov.web.ui;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import gr.hua.dit.project.mycitygov.core.service.AppointmentService;
import gr.hua.dit.project.mycitygov.core.service.RequestService;
import gr.hua.dit.project.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.project.mycitygov.core.service.model.TicketListItem;
import org.springframework.security.core.Authentication;

@Controller
public class HomepageController {

   private final AppointmentService appointmentService;
   private final RequestService requestService;

   public HomepageController(final AppointmentService appointmentService, final RequestService requestService) {
      this.appointmentService = appointmentService;
      this.requestService = requestService;
   }

   @GetMapping("/")
   public String showHomepage(final Authentication authentication, final Model model) {
      final boolean authenticated = AuthUtils.isAuthenticated(authentication);
      if (authenticated) {
         final List<TicketListItem> tickets = this.requestService.getTicketsForCurrentUser();
         final List<AppointmentView> appointments = this.appointmentService.listForCurrentUser();
         model.addAttribute("ticketPreview", tickets.stream().limit(3).toList());
         model.addAttribute("appointmentPreview", appointments.stream().limit(3).toList());
      } else {
         model.addAttribute("ticketPreview", List.of());
         model.addAttribute("appointmentPreview", List.of());
      }
      model.addAttribute("isAuthenticated", authenticated);
      return "homepage";
   }
}
