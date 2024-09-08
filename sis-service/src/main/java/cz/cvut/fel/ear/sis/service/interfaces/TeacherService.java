package cz.cvut.fel.ear.sis.service.interfaces;
import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.rest.dto.EnrollmentRequest;
import cz.cvut.fel.ear.sis.utils.enums.*;
import cz.cvut.fel.ear.sis.utils.exception.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface TeacherService {
    Course createCourse(long teacherId, String courseName, String code, int ECTS, Locale language) throws CourseException, PersonException;

    Parallel createParallel(long teacherId, int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek, long semesterId, long classroomId, long courseId) throws CourseException, PersonException, ParallelException, ClassroomException, SemesterException;

    void updateCourse(long courseId, long teacherId, String courseName, String code, int ECTS, Locale language) throws CourseException, PersonException;

    void gradeStudent(String studentUsername, EnrollmentRequest enrollmentRequest, Teacher teacher) throws StudentException;

    void updateParallel(Parallel parallel, Teacher teacher, int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek, Semester semester, Classroom classroom, Course course) throws PersonException, ParallelException, SemesterException, ClassroomException;

    List<Course> getAllCoruses();

    List<Parallel> getAllParallels();

    List<Parallel> getNextSemesterTeacherParallels(long teacherId) throws SemesterException;

    List<Teacher> getAllTeachers();

    Optional<Teacher> getTeacherById(Long id);

    Optional<Course> getCourseById(Long id);

    List<Course> getCourseByTeacherId(Long id);

    Optional<Parallel> getParallelById(Long id);

    List<Parallel> getParallelByCourseId(Long id);

    List<Student> getAllStudentsByParallelId(Long id) throws ParallelException;

    List<Course> getCoursesByTeacherUsername(String username);

    Teacher getTeacherByUsername(String username);
}
