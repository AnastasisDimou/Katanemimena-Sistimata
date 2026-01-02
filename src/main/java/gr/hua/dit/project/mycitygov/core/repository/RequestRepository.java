package gr.hua.dit.project.mycitygov.core.repository;

import gr.hua.dit.project.mycitygov.core.model.Request;
import gr.hua.dit.project.mycitygov.core.model.RequestType;
import gr.hua.dit.project.mycitygov.core.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
   Optional<Request> findByProtocolNumber(String protocolNumber);

   List<Request> findByRequestType(RequestType requestType);

   List<Request> findByCitizen(User citizen);

   List<Request> findByCitizenOrderBySubmissionDateDesc(User citizen);

   List<Request> findByRequestType_ServiceDepartmentOrderBySubmissionDateDesc(ServiceDepartment department);

   List<Request> findAllByOrderBySubmissionDateDesc();

   boolean existsByProtocolNumber(String protocolNumber);

}
