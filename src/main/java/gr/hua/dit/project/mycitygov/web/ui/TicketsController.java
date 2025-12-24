package gr.hua.dit.project.mycitygov.web.ui;

import gr.hua.dit.project.mycitygov.core.repository.ServiceDepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.Objects;

@Controller
public class TicketsController {

   private final ServiceDepartmentRepository serviceDepartmentRepository;

   public TicketsController(final ServiceDepartmentRepository serviceDepartmentRepository) {
      this.serviceDepartmentRepository = Objects.requireNonNull(serviceDepartmentRepository);
   }

   @GetMapping("/tickets")
   public String listTickets(final Model model) {
      model.addAttribute("serviceDepartments", this.serviceDepartmentRepository.findAll());
      model.addAttribute("requests", Collections.emptyList());
      return "tickets";
   }

   @GetMapping("/tickets/new")
   public String newTicketForm(final Model model) {
      model.addAttribute("serviceDepartments", this.serviceDepartmentRepository.findAll());
      model.addAttribute("openRequest", new OpenTicketForm());
      return "new_ticket";
   }

   public static final class OpenTicketForm {
      private Long departmentId;
      private String subject;
      private String content;
      private String attachmentUrl;

      public Long getDepartmentId() {
         return departmentId;
      }

      public void setDepartmentId(final Long departmentId) {
         this.departmentId = departmentId;
      }

      public String getSubject() {
         return subject;
      }

      public void setSubject(final String subject) {
         this.subject = subject;
      }

      public String getContent() {
         return content;
      }

      public void setContent(final String content) {
         this.content = content;
      }

      public String getAttachmentUrl() {
         return attachmentUrl;
      }

      public void setAttachmentUrl(final String attachmentUrl) {
         this.attachmentUrl = attachmentUrl;
      }
   }
}
