package gr.hua.dit.project.mycitygov.web.ui;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gr.hua.dit.project.mycitygov.core.model.RequestType;
import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;
import gr.hua.dit.project.mycitygov.core.service.AdminCatalogService;
import gr.hua.dit.project.mycitygov.core.service.model.RequestTypeAdminView;
import gr.hua.dit.project.mycitygov.web.ui.model.DepartmentForm;
import gr.hua.dit.project.mycitygov.web.ui.model.RequestTypeForm;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCatalogController {

   private final AdminCatalogService adminCatalogService;

   public AdminCatalogController(final AdminCatalogService adminCatalogService) {
      this.adminCatalogService = adminCatalogService;
   }

   @GetMapping("")
   public String adminHome(final Model model) {
      model.addAttribute("departmentsCount", this.adminCatalogService.listDepartments().size());
      model.addAttribute("requestTypesCount", this.adminCatalogService.listRequestTypes().size());
      return "admin_home";
   }

   @GetMapping("/departments")
   public String showDepartments(final Model model) {
      loadDepartments(model);
      if (!model.containsAttribute("departmentForm")) {
         model.addAttribute("departmentForm", new DepartmentForm("", "", ""));
      }
      return "admin_departments";
   }

   @PostMapping("/departments")
   public String createDepartment(
         @ModelAttribute("departmentForm") @Valid final DepartmentForm departmentForm,
         final BindingResult bindingResult,
         final Model model,
         final RedirectAttributes redirectAttributes) {
      if (bindingResult.hasErrors()) {
         loadDepartments(model);
         return "admin_departments";
      }
      try {
         this.adminCatalogService.createDepartment(departmentForm);
         redirectAttributes.addFlashAttribute("successMessage", "Το τμήμα δημιουργήθηκε.");
      } catch (Exception ex) {
         redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
      }
      return "redirect:/admin/departments";
   }

   @PostMapping("/departments/{id}/delete")
   public String deleteDepartment(
         @PathVariable("id") final Long departmentId,
         final RedirectAttributes redirectAttributes) {
      try {
         this.adminCatalogService.deleteDepartment(departmentId);
         redirectAttributes.addFlashAttribute("successMessage", "Το τμήμα διαγράφηκε.");
      } catch (Exception ex) {
         redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
      }
      return "redirect:/admin/departments";
   }

   @GetMapping("/request-types")
   public String showRequestTypes(final Model model) {
      loadRequestTypes(model);
      if (!model.containsAttribute("requestTypeForm")) {
         model.addAttribute("requestTypeForm", new RequestTypeForm("", "", "", null, null));
      }
      return "admin_request_types";
   }

   @PostMapping("/request-types")
   public String createRequestType(
         @ModelAttribute("requestTypeForm") @Valid final RequestTypeForm requestTypeForm,
         final BindingResult bindingResult,
         final Model model,
         final RedirectAttributes redirectAttributes) {
      if (bindingResult.hasErrors()) {
         loadRequestTypes(model);
         return "admin_request_types";
      }
      try {
         this.adminCatalogService.createRequestType(requestTypeForm);
         redirectAttributes.addFlashAttribute("successMessage", "Ο τύπος αιτήματος δημιουργήθηκε.");
      } catch (Exception ex) {
         redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
      }
      return "redirect:/admin/request-types";
   }

   @PostMapping("/request-types/{id}/toggle")
   public String toggleRequestType(
         @PathVariable("id") final Long requestTypeId,
         final RedirectAttributes redirectAttributes) {
         try {
            this.adminCatalogService.toggleRequestTypeActive(requestTypeId);
            redirectAttributes.addFlashAttribute("successMessage", "Η κατάσταση του τύπου αιτήματος ενημερώθηκε.");
         } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
         }
      return "redirect:/admin/request-types";
   }

   @PostMapping("/request-types/{id}/delete")
   public String deleteRequestType(
         @PathVariable("id") final Long requestTypeId,
         final RedirectAttributes redirectAttributes) {
         try {
            this.adminCatalogService.deleteRequestType(requestTypeId);
            redirectAttributes.addFlashAttribute("successMessage", "Ο τύπος αιτήματος διαγράφηκε.");
         } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
         }
      return "redirect:/admin/request-types";
   }

   private void loadDepartments(final Model model) {
      final List<ServiceDepartment> departments = this.adminCatalogService.listDepartments();
      model.addAttribute("departments", departments);
   }

   private void loadRequestTypes(final Model model) {
      final List<ServiceDepartment> departments = this.adminCatalogService.listDepartments();
      final List<RequestTypeAdminView> requestTypes = this.adminCatalogService.listRequestTypes();
      model.addAttribute("departments", departments);
      model.addAttribute("requestTypes", requestTypes);
   }
}
