package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    Optional<Classroom> findClassroomByCode(String code);

}
