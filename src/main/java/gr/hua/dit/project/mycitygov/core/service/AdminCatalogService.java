package gr.hua.dit.project.mycitygov.core.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.dit.project.mycitygov.core.model.RequestType;
import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import gr.hua.dit.project.mycitygov.core.repository.RequestTypeRepository;
import gr.hua.dit.project.mycitygov.core.repository.ServiceDepartmentRepository;
import gr.hua.dit.project.mycitygov.core.security.CurrentUser;
import gr.hua.dit.project.mycitygov.core.security.CurrentUserProvider;
import gr.hua.dit.project.mycitygov.web.ui.model.DepartmentForm;
import gr.hua.dit.project.mycitygov.web.ui.model.RequestTypeForm;
import gr.hua.dit.project.mycitygov.core.service.model.RequestTypeAdminView;

@Service
public class AdminCatalogService {

   private final ServiceDepartmentRepository serviceDepartmentRepository;
   private final RequestTypeRepository requestTypeRepository;
   private final CurrentUserProvider currentUserProvider;

   public AdminCatalogService(
         final ServiceDepartmentRepository serviceDepartmentRepository,
         final RequestTypeRepository requestTypeRepository,
         final CurrentUserProvider currentUserProvider) {
      this.serviceDepartmentRepository = Objects.requireNonNull(serviceDepartmentRepository);
      this.requestTypeRepository = Objects.requireNonNull(requestTypeRepository);
      this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
   }

   @Transactional(readOnly = true)
   public List<ServiceDepartment> listDepartments() {
      requireAdmin();
      return this.serviceDepartmentRepository.findAll();
   }

   @Transactional
   public ServiceDepartment createDepartment(final DepartmentForm form) {
      Objects.requireNonNull(form, "form cannot be null");
      requireAdmin();

      final ServiceDepartment dept = new ServiceDepartment();
      dept.setCode(form.code().trim());
      dept.setName(form.name().trim());
      dept.setDescription(form.description());
      dept.setAppointmentStartTime(LocalTime.of(9, 0));
      dept.setAppointmentEndTime(LocalTime.of(17, 0));

      return this.serviceDepartmentRepository.save(dept);
   }

   @Transactional
   public ServiceDepartment updateDepartmentHours(
         final Long departmentId,
         final LocalTime startTime,
         final LocalTime endTime) {
      if (departmentId == null) {
         throw new IllegalArgumentException("departmentId cannot be null");
      }
      if (startTime == null || endTime == null) {
         throw new IllegalArgumentException("startTime/endTime cannot be null");
      }
      if (!startTime.isBefore(endTime)) {
         throw new IllegalArgumentException("Η ώρα έναρξης πρέπει να είναι πριν από την ώρα λήξης.");
      }
      requireAdmin();

      final ServiceDepartment dept = this.serviceDepartmentRepository.findById(departmentId)
            .orElseThrow(() -> new IllegalArgumentException("Department not found"));
      dept.setAppointmentStartTime(startTime);
      dept.setAppointmentEndTime(endTime);
      return this.serviceDepartmentRepository.save(dept);
   }

   @Transactional
   public void deleteDepartment(final Long departmentId) {
      if (departmentId == null) {
         throw new IllegalArgumentException("departmentId cannot be null");
      }
      requireAdmin();

      try {
         this.serviceDepartmentRepository.deleteById(departmentId);
      } catch (DataIntegrityViolationException ex) {
         throw new IllegalStateException("Το τμήμα χρησιμοποιείται σε άλλα δεδομένα και δεν μπορεί να διαγραφεί", ex);
      }
   }

   @Transactional(readOnly = true)
   public List<RequestTypeAdminView> listRequestTypes() {
      requireAdmin();
      return this.requestTypeRepository.findAll().stream()
            .map(rt -> new RequestTypeAdminView(
                  rt.getId(),
                  rt.getProtocolNumber(),
                  rt.getName(),
                  rt.getDescription(),
                  rt.getMaxProcessingDays(),
                  rt.isActive(),
                  rt.getServiceDepartment().getId(),
                  rt.getServiceDepartment().getName()))
            .toList();
   }

   @Transactional
   public RequestType createRequestType(final RequestTypeForm form) {
      Objects.requireNonNull(form, "form cannot be null");
      requireAdmin();

      final ServiceDepartment dept = this.serviceDepartmentRepository.findById(form.departmentId())
            .orElseThrow(() -> new IllegalArgumentException("Department not found"));

      final RequestType rt = new RequestType();
      rt.setProtocolNumber(form.protocolNumber().trim());
      rt.setName(form.name().trim());
      rt.setDescription(form.description());
      rt.setActive(true);
      rt.setMaxProcessingDays(form.maxProcessingDays());
      rt.setServiceDepartment(dept);

      return this.requestTypeRepository.save(rt);
   }

   @Transactional
   public void deleteRequestType(final Long requestTypeId) {
      if (requestTypeId == null) {
         throw new IllegalArgumentException("requestTypeId cannot be null");
      }
      requireAdmin();
      try {
         this.requestTypeRepository.deleteById(requestTypeId);
      } catch (DataIntegrityViolationException ex) {
         throw new IllegalStateException("Ο τύπος αιτήματος χρησιμοποιείται και δεν μπορεί να διαγραφεί", ex);
      }
   }

   @Transactional
   public RequestType toggleRequestTypeActive(final Long requestTypeId) {
      if (requestTypeId == null) {
         throw new IllegalArgumentException("requestTypeId cannot be null");
      }
      requireAdmin();
      final RequestType rt = this.requestTypeRepository.findById(requestTypeId)
            .orElseThrow(() -> new IllegalArgumentException("Request type not found"));
      rt.setActive(!rt.isActive());
      return this.requestTypeRepository.save(rt);
   }

   private void requireAdmin() {
      final CurrentUser me = this.currentUserProvider.requireCurrentUser();
      if (me.type() != UserType.ADMIN) {
         throw new SecurityException("Admin role required");
      }
   }
}
