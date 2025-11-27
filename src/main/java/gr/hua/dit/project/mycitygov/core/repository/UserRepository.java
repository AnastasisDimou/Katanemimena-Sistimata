package gr.hua.dit.project.mycitygov.core.repository;

import gr.hua.dit.project.mycitygov.core.model.User;
import gr.hua.dit.project.mycitygov.core.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   User findByEmail(String email);

   boolean existsByAfm(String afm);

   boolean existsByAmka(String amka);

   boolean existsByEmail(String email);

}
