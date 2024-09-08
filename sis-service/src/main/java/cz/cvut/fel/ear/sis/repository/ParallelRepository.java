package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ParallelRepository extends JpaRepository<Parallel, Long> {

    List<Parallel> findByClassroomAndSemesterAndDayOfWeekAndTimeSlot(Classroom classroom, Semester semester, DayOfWeek dayOfWeek, TimeSlot timeSlot);

    List<Parallel> findAllBySemesterStartDate(LocalDate startDate);

    List<Parallel> findAllByCourseId(Long id);

    @Query( "SELECT p FROM Parallel p" +
            " WHERE p.course.code = :courseCode")
    List<Parallel> findAllByCourseCode(String courseCode);

    @Query( "SELECT p FROM Parallel p " +
            "JOIN p.students s " +
            "WHERE s.id = :studentId " +
            "AND p.semester.code = :semesterCode")
    List<Parallel> findAllByStudentIdAndSemesterCode(long studentId, String semesterCode);

    @Query("SELECT p FROM Parallel p " +
            "JOIN p.students s " +
            "WHERE s.userName = :studentUsername " +
            "AND p.semester.code = :semesterCode")
    List<Parallel> findAllByStudentUsernameAndSemesterCode(String studentUsername, String semesterCode);

    @Query("SELECT p FROM Parallel p " +
            "JOIN p.students s " +
            "WHERE s.userName = :studentUsername "
    )
    List<Parallel> findAllByStudentsUsername(String studentUsername);

    @Query("SELECT p FROM Parallel p " +
            "JOIN p.students s " +
            "WHERE s.userName = :studentUsername " +
            "AND p.course.id = :courseId"
    )
    Parallel findByStudentUsernameAndCourseId(String studentUsername, Long courseId);

}
