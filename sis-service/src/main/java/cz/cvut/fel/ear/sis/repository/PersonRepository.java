package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByUserName(String userName);

    Person findByUserName(String userName);
}
