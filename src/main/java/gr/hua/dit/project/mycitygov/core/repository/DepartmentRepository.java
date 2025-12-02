package gr.hua.dit.project.mycitygov.core.repository;

import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<ServiceDepartment, Long> {
   Optional<ServiceDepartment> findByCode(String code);

   Optional<ServiceDepartment> findByName(String name);

   boolean existsByCode(String code);

   boolean existsByName(String name);

}
