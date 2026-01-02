package gr.hua.dit.project.mycitygov.web.ui;

import gr.hua.dit.project.mycitygov.core.model.RequestStatus;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.RequestTypeRepository;
import gr.hua.dit.project.mycitygov.core.security.CurrentUser;
import gr.hua.dit.project.mycitygov.core.security.CurrentUserProvider;
import gr.hua.dit.project.mycitygov.core.service.RequestService;
import gr.hua.dit.project.mycitygov.core.service.model.TicketDetailsView;
import gr.hua.dit.project.mycitygov.core.service.model.TicketListItem;
import gr.hua.dit.project.mycitygov.web.ui.model.OpenTicketForm;
import gr.hua.dit.project.mycitygov.web.ui.model.UpdateTicketStatusForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketsController {

   private final RequestService requestService;
   private final RequestTypeRepository requestTypeRepository;
   private final CurrentUserProvider currentUserProvider;

   public TicketsController(
         final RequestService requestService,
         final RequestTypeRepository requestTypeRepository,
         final CurrentUserProvider currentUserProvider) {
      this.requestService = requestService;
      this.requestTypeRepository = requestTypeRepository;
      this.currentUserProvider = currentUserProvider;
   }

   @GetMapping("")
   public String listTickets(final Model model) {
      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      final List<TicketListItem> tickets = switch (me.type()) {
         case CITIZEN -> this.requestService.getCitizenTickets();
         case EMPLOYEE -> this.requestService.getDepartmentTickets();
         case ADMIN -> this.requestService.getAllTickets();
      };
      model.addAttribute("requests", tickets);
      return "tickets";
   }

   @GetMapping("/new")
   public String newTicketForm(final Model model) {
      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      if (me.type() != UserType.CITIZEN) {
         return "redirect:/tickets";
      }
      model.addAttribute("requestTypes", this.requestTypeRepository.findByActiveTrueOrderByNameAsc());
      model.addAttribute("openRequest", new OpenTicketForm(null, "", "", ""));
      return "new_ticket";
   }

   @PostMapping("/new")
   public String handleNewTicket(
         @ModelAttribute("openRequest") @Valid final OpenTicketForm openTicketForm,
         final BindingResult bindingResult,
         final Model model) {
      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      if (me.type() != UserType.CITIZEN) {
         return "redirect:/tickets";
      }
      if (bindingResult.hasErrors()) {
         model.addAttribute("requestTypes", this.requestTypeRepository.findByActiveTrueOrderByNameAsc());
         return "new_ticket";
      }
      final TicketDetailsView created = this.requestService.createCitizenTicket(openTicketForm);
      return "redirect:/tickets/" + created.id();
   }

   @GetMapping("/{id}")
   public String showTicket(@PathVariable("id") final Long id, final Model model) {
      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      final TicketDetailsView requestView = switch (me.type()) {
         case CITIZEN -> this.requestService.getCitizenTicket(id);
         case EMPLOYEE -> this.requestService.getEmployeeTicket(id);
         case ADMIN -> this.requestService.getEmployeeTicket(id);
      };
      model.addAttribute("request", requestView);
      model.addAttribute("statusForm", new UpdateTicketStatusForm(RequestStatus.IN_PROGRESS, ""));
      return "ticket";
   }

   @PostMapping("/{id}/assign")
   public String assignToSelf(@PathVariable("id") final Long id) {
      this.requestService.assignToCurrentEmployee(id);
      return "redirect:/tickets/" + id;
   }

   @PostMapping("/{id}/status")
   public String updateStatus(
         @PathVariable("id") final Long id,
         @ModelAttribute("statusForm") @Valid final UpdateTicketStatusForm statusForm,
         final BindingResult bindingResult,
         final Model model) {
      if (bindingResult.hasErrors()) {
         final TicketDetailsView requestView = this.requestService.getEmployeeTicket(id);
         model.addAttribute("request", requestView);
         return "ticket";
      }
      final TicketDetailsView updated = this.requestService.updateStatus(id, statusForm);
      return "redirect:/tickets/" + updated.id();
   }
}
