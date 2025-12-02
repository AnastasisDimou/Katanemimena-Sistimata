package gr.hua.dit.project.mycitygov.core.port.repository;

import gr.hua.dit.project.mycitygov.core.model.RequestType;
import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType, Long> {
   Optional<RequestType> findByProtocolNumber(String protocolNumber);

   Optional<RequestType> findByName(String name);

   List<RequestType> findByServiceDepartmentAndActiveTrue(ServiceDepartment department);

   boolean existsByProtocolNumber(String protocolNumber);

}
