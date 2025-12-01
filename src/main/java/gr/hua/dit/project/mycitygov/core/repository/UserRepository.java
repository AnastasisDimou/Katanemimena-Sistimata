package gr.hua.dit.project.mycitygov.core.repository;

import gr.hua.dit.project.mycitygov.core.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);

   Optional<User> findByAfm(String afm);

   Optional<User> findByAmka(String amka);

   Optional<User> findByPhoneNumber(String phoneNumber);

   boolean existsByAfm(String afm);

   boolean existsByAmka(String amka);

   boolean existsByEmail(String email);

}
