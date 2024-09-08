package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {

    Teacher findByUserName(String userName);

}
