package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByTeacher_Id(Long teacherId);

    @Query("SELECT c FROM Course c WHERE c.teacher.userName = :username")
    List<Course> findCoursesByTeacher(@Param("username") String teacherUsername);
}
