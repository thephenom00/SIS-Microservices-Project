package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findSemesterByCode(String code);

    Optional<Semester> findSemesterByIsActiveIsTrue();
}
