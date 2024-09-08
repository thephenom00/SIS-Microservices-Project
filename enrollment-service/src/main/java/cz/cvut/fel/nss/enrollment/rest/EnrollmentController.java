package cz.cvut.fel.nss.enrollment.rest;

import cz.cvut.fel.nss.enrollment.model.Enrollment;
import cz.cvut.fel.nss.enrollment.rest.dto.EnrollmentRequest;
import cz.cvut.fel.nss.enrollment.service.EnrollmentService;
import cz.cvut.fel.nss.enrollment.utils.exceptions.EnrollmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollment")
@EnableCaching
public class EnrollmentController {

    private EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * Retrieves enrollments by username.
     *
     * @param username The username of the student.
     * @return List of enrollments.
     */
    @Cacheable(key = "#username", value = "enrollments")
    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Enrollment>> getEnrollmentsByUsername(@PathVariable String username) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByUsername(username);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    /**
     * Retrieves all enrollments.
     *
     * @return List of enrollments.
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Enrollment>> getAllEnrollments() {
        Iterable<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    /**
     * Create enrollment by username.
     *
     * @param username The username of the student.
     * @param enrollmentRequest The request body containing details to create a new enrollment.
     */
    @PostMapping(value = "/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = "enrollments", key = "#username")
    public ResponseEntity<Void> createEnrollmentByUsername(@PathVariable String username, @RequestBody EnrollmentRequest enrollmentRequest) {
        enrollmentService.createEnrollmentByUsername(username, enrollmentRequest.getCourse(), enrollmentRequest.getTeacherName(), enrollmentRequest.getParallelId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Grade student by username.
     *
     * @param username The username of the student.
     * @param enrollmentRequest The request body containing details to grade a student.
     */
    @PostMapping(value = "/grade/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = "enrollments", key = "#username")
    public ResponseEntity<Void> gradeStudentByUsername(@PathVariable String username, @RequestBody EnrollmentRequest enrollmentRequest) throws EnrollmentException {
        enrollmentService.gradeStudentByUsername(username, enrollmentRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Remove enrollment.
     *
     * @param username The username of the student.
     * @return Response entity.
     */
    @DeleteMapping(value = "/{username}/{parallelId}")
    @CacheEvict(value = "enrollments", key = "#username")
    public ResponseEntity<Void> deleteEnrollmentsByUsername(@PathVariable String username, @PathVariable Long parallelId) throws EnrollmentException {
        enrollmentService.deleteEnrollmentByUsername(username, parallelId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
