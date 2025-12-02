package gr.hua.dit.project.mycitygov.core.port.repository;

import gr.hua.dit.project.mycitygov.core.model.Appointment;
import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.ServiceDepartment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
   Optional<Appointment> findByProtocolNumber(String protocolNumber);

   List<Appointment> findByCitizen(User citizen);

   List<Appointment> findByServiceDepartment(ServiceDepartment department);

   boolean existsByProtocolNumber(String protocolNumber);

}
