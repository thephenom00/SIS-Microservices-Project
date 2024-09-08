package cz.cvut.fel.ear.sis.rest;


import cz.cvut.fel.ear.sis.dto.EnrollmentDto;
import cz.cvut.fel.ear.sis.dto.ParallelDto;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.service.StudentServiceImpl;
import cz.cvut.fel.ear.sis.service.TeacherServiceImpl;
import cz.cvut.fel.ear.sis.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


import org.springframework.http.HttpStatus;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/student")
public class StudentController {

    private StudentServiceImpl studentServiceImpl;
    private TeacherServiceImpl teacherServiceImpl;

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    public StudentController(StudentServiceImpl studentServiceImpl, TeacherServiceImpl teacherServiceImpl) {
        this.studentServiceImpl = studentServiceImpl;
        this.teacherServiceImpl = teacherServiceImpl;
    }

    /**
     * Retrieves all available parallels for the next semester.
     *
     * @return List of parallels for the next semester.
     * @throws ParallelException If no parallels are found.
     * @throws SemesterException If there's an issue with the semester data.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/course/next", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParallelDto>> listCoursesForNextSemester() throws ParallelException, SemesterException {
        List<Parallel> parallels = studentServiceImpl.getAllParallelsForNextSemester();
        List<ParallelDto> parallelDtos = teacherServiceImpl.mapParallelsToDto(parallels);

        if (parallels.isEmpty()) {
            throw new ParallelException("No parallels found for the next semester.");
        }

        return new ResponseEntity<>(parallelDtos, HttpStatus.OK);
    }

    /**
     * Retrieves all available parallels for the current semester.
     *
     * @return List of parallels for the current semester.
     * @throws ParallelException If no parallels are found.
     * @throws SemesterException If there's an issue with the semester data.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/course/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParallelDto>> listCoursesForCurrentSemester() throws ParallelException, SemesterException {
        List<Parallel> parallels = studentServiceImpl.getAllParallelsForCurrentSemester();
        List<ParallelDto> parallelDtos = teacherServiceImpl.mapParallelsToDto(parallels);

        if(parallels.isEmpty()){
            throw new ParallelException("No parallels found for the next semester.");
        }
        return new ResponseEntity<>(parallelDtos, HttpStatus.OK);
    }

    /**
     * Retrieves the student's schedule for a specific semester.
     *
     * @param semesterCode The code of the semester.
     * @param auth         The student's authentication details.
     * @return List of enrolled parallels for the specified semester.
     * @throws ParallelException If no enrolled parallels are found.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/schedule/{semesterCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParallelDto>> viewScheduleForSemester(@PathVariable String semesterCode, Authentication auth) throws ParallelException {
        User user = (User) auth.getPrincipal();
        List<Parallel> parallels = studentServiceImpl.getAllEnrolledParallelsForNextSemesterByStudentUsername(user.getUsername(), semesterCode);
        List<ParallelDto> parallelDtos = teacherServiceImpl.mapParallelsToDto(parallels);

        if (parallels.isEmpty()) {
            throw new ParallelException("You have not enrolled in any parallels for the given semester.");
        }
        return new ResponseEntity<>(parallelDtos, HttpStatus.OK);
    }

    /**
     * Retrieves the student's current schedule.
     *
     * @param auth The student's authentication details.
     * @return List of currently enrolled parallels.
     * @throws ParallelException If no enrolled parallels are found.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParallelDto>> viewSchedule(Authentication auth) throws ParallelException {
        User user = (User) auth.getPrincipal();
        List<Parallel> parallels = studentServiceImpl.getAllStudentsEnrolledParallels(user.getUsername());
        List<ParallelDto> parallelDtos = teacherServiceImpl.mapParallelsToDto(parallels);

        if (parallels.isEmpty()) {
            throw new ParallelException("You have not enrolled in any parallels for the given semester.");
        }
        return new ResponseEntity<>(parallelDtos, HttpStatus.OK);
    }

    /**
     * Retrieves parallels for a specific course in the next semester.
     *
     * @param courseCode The course identifier.
     * @return List of parallels for the course in the next semester.
     * @throws SemesterException If there's an issue with semester data.
     * @throws ParallelException If no parallels are found for the course.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/parallel/{courseCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ParallelDto>> listParallelsForCourseNextSemester(@PathVariable String courseCode) throws SemesterException, ParallelException {
        List<Parallel> parallels = studentServiceImpl.getParallelsForCourseNextSemester(courseCode);
        List<ParallelDto> parallelDtos = teacherServiceImpl.mapParallelsToDto(parallels);

        if (parallels.isEmpty()) {
            throw new ParallelException("No parallels found for the given course.");
        }

        return new ResponseEntity<>(parallelDtos, HttpStatus.OK);
    }

    /**
     * Retrieves the student's enrollment report.
     *
     * @param auth The student's authentication details.
     * @return List of the student's enrollments.
     * @throws EnrollmentException If no enrollments are found.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EnrollmentDto>> viewEnrollmentReport(Authentication auth) throws EnrollmentException {
        User user = (User) auth.getPrincipal();
        List<EnrollmentDto> report = studentServiceImpl.getEnrollmentReportByUsername(user.getUsername());

        if (report.isEmpty()) {
            throw new EnrollmentException("No enrollments found for the student.");
        }

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    /**
     * Enrolls the student in a specific parallel for the next semester.
     *
     * @param parallelId The parallel identifier.
     * @param auth       The student's authentication details.
     * @throws EnrollmentException If there's an issue enrolling the student.
     * @throws ParallelException   If the parallel is invalid or unavailable.
     * @throws StudentException    If the student data is invalid.
     * @throws SemesterException   If the semester data is invalid.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping(value = "/enroll/{parallelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enrollInParallelNextSemester(@PathVariable Long parallelId, Authentication auth) throws EnrollmentException, ParallelException, StudentException, SemesterException {
        User user = (User) auth.getPrincipal();
        studentServiceImpl.enrollToParallelByUsername(user.getUsername(), parallelId);

        logger.debug("Enrolled student {} in parallel {} for the next semester.", user.getUsername(), parallelId);
    }

    /**
     * Cancels the student's enrollment in a specific parallel for the next semester.
     *
     * @param parallelId The parallel identifier.
     * @param auth       The student's authentication details.
     * @throws ParallelException If the parallel is invalid or unavailable.
     * @throws StudentException  If the student data is invalid.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @DeleteMapping(value = "/enroll/{parallelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revertEnrollment(@PathVariable Long parallelId, Authentication auth) throws ParallelException, StudentException {
        User user = (User) auth.getPrincipal();
        studentServiceImpl.dropFromParallelByUsername(user.getUsername(), parallelId);
        logger.debug("Cancelled enrollment for student {} in parallel {} for next semester.", user.getUsername(), parallelId);
    }

}

