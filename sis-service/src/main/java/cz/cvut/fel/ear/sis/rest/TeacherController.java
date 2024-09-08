package cz.cvut.fel.ear.sis.rest;

import cz.cvut.fel.ear.sis.dto.CourseDto;
import cz.cvut.fel.ear.sis.dto.ParallelDto;
import cz.cvut.fel.ear.sis.dto.PersonDto;
import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.rest.dto.EnrollmentRequest;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.service.TeacherServiceImpl;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.Grade;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import cz.cvut.fel.ear.sis.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;


import org.springframework.http.HttpStatus;

import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private TeacherServiceImpl teacherServiceImpl;

    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    public TeacherController(TeacherServiceImpl teacherServiceImpl) {
        this.teacherServiceImpl = teacherServiceImpl;
    }

    /**
     * Retrieves all teacher's courses.
     *
     * @param auth The authentication details of the teacher.
     * @return List of courses associated with the teacher.
     * @throws CourseException If no courses are found.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CourseDto>> listMyCourses(Authentication auth) throws CourseException {
        User user = (User) auth.getPrincipal();
        List<Course> courses = teacherServiceImpl.getCoursesByTeacherUsername(user.getUsername());

        if (courses.isEmpty()){
            throw new CourseException("No courses found for the given teacher.");
        }

        List<CourseDto> coursesDto = teacherServiceImpl.mapCoursesToDto(courses);
        return new ResponseEntity<>(coursesDto, HttpStatus.OK);
    }

    /**
     * Retrieves all parallels associated with a given course.
     *
     * @param courseId The unique identifier for the course.
     * @return List of parallels associated with the course.
     * @throws ParallelException If no parallels are found.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/parallel/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParallelDto>> listParallelsForCourse(@PathVariable Long courseId) throws ParallelException {
        List<Parallel> parallels = teacherServiceImpl.getParallelByCourseId(courseId);

        if (parallels.isEmpty()) {
            throw new ParallelException("No parallels found for the given course.");
        }

        List<ParallelDto> parallelsDto = teacherServiceImpl.mapParallelsToDto(parallels);
        return new ResponseEntity<>(parallelsDto, HttpStatus.OK);
    }

    /**
     * Retrieves all students enrolled in a specific parallel.
     *
     * @param parallelId The unique identifier for the parallel.
     * @return List of students enrolled in the parallel.
     * @throws ParallelException If no parallels are found.
     * @throws StudentException If no students are found.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/students/{parallelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonDto>> listStudentsForParallel(@PathVariable Long parallelId) throws StudentException, ParallelException {
        List<Student> students = teacherServiceImpl.getAllStudentsByParallelId(parallelId);

        if (students.isEmpty()){
            throw new StudentException("No students found for the given parallel.");
        }

        List<PersonDto> studentsDto = teacherServiceImpl.mapStudentsToDto(students);
        return new ResponseEntity<>(studentsDto, HttpStatus.OK);
    }

    /**
     * Grades a student in a specific parallel.
     *
     * @param studentUsername The username of the student.
     * @param enrollmentRequest The request containing the grade.
     * @throws StudentException If there's an issue with the student.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/grade/{studentUsername}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void gradeStudent(
                             @PathVariable String studentUsername,
                             @RequestBody EnrollmentRequest enrollmentRequest) throws StudentException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Teacher teacher = teacherServiceImpl.getTeacherByUsername(user.getUsername());
        try {
            Grade.valueOf(enrollmentRequest.getGrade());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid grade format", e);
        }


        teacherServiceImpl.gradeStudent(studentUsername, enrollmentRequest, teacher);
        logger.debug("Graded student {}", studentUsername);
    }

    /**
     * Creates a new course.
     *
     * @param courseName The name of the course.
     * @param code The code of the course.
     * @param ECTS The ECTS credits for the course.
     * @param language The language tag for the course.
     * @return Response entity with headers indicating the location of the newly created course.
     * @throws CourseException If there's an issue with the course.
     * @throws PersonException If there's an issue related to the person.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/course", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> createCourse(@RequestParam String courseName,
                                               @RequestParam String code,
                                               @RequestParam int ECTS,
                                               @RequestParam String language) throws CourseException, PersonException {
        Locale locale;
        if (language != null) {
            try {
                locale = Locale.forLanguageTag(language);
            } catch (IllformedLocaleException e) {
                throw new IllegalArgumentException("Invalid locale format", e);
            }
        } else {
            locale = Locale.getDefault();
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long teacherId = teacherServiceImpl.getTeacherByUsername(user.getUsername()).getId();
        teacherServiceImpl.createCourse(teacherId, courseName, code, ECTS, locale);
        logger.debug("Created course {}.", courseName);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", teacherId);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Creates a new parallel for a given course.
     *
     * @param courseId The unique identifier for the course.
     * @param capacity The capacity of the parallel.
     * @param timeSlot The time slot for the parallel.
     * @param dayOfWeek The day of the week for the parallel.
     * @param semesterId The unique identifier for the semester.
     * @param classroomId The unique identifier for the classroom.
     * @return Response entity with headers indicating the location of the newly created parallel.
     * @throws SemesterException If there's an issue with the semester.
     * @throws ParallelException If there's an issue with the parallel.
     * @throws ClassroomException If there's an issue with the classroom.
     * @throws CourseException If there's an issue with the course.
     * @throws PersonException If there's an issue with the person.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/parallel/{courseId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<HttpStatus> createParallel(@PathVariable Long courseId,
                                                   @RequestParam int capacity,
                                                   @RequestParam String timeSlot,
                                                   @RequestParam String dayOfWeek,
                                                   @RequestParam long semesterId,
                                                   @RequestParam long classroomId)
            throws SemesterException, ParallelException, ClassroomException, CourseException, PersonException {
        logger.info("Received request with courseId: {}, capacity: {}, timeSlot: {}, dayOfWeek: {}, semesterId: {}, classroomId: {}",
                courseId, capacity, timeSlot, dayOfWeek, semesterId, classroomId);
        Course course = teacherServiceImpl.getCourseById(courseId).orElseThrow(() -> new CourseException("Course not found"));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long teacherId = teacherServiceImpl.getTeacherByUsername(user.getUsername()).getId();
        Parallel parallel = teacherServiceImpl.createParallel(teacherId, capacity, TimeSlot.valueOf(timeSlot),
                DayOfWeek.valueOf(dayOfWeek), semesterId, classroomId, courseId);
        logger.debug("Created parallel {} for course {}.", parallel.getId(), course.getName());

        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", parallel.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

}
