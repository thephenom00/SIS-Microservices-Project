package cz.cvut.fel.nss.enrollment.service;

import cz.cvut.fel.nss.enrollment.model.Enrollment;
import cz.cvut.fel.nss.enrollment.repository.EnrollmentRepository;
import cz.cvut.fel.nss.enrollment.rest.dto.EnrollmentRequest;
import cz.cvut.fel.nss.enrollment.utils.enums.Grade;
import cz.cvut.fel.nss.enrollment.utils.exceptions.EnrollmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Retrieves all enrollments by username.
     *
     * @param username The username of the student.
     * @return List of Enrollments.
     */
    public List<Enrollment> getEnrollmentsByUsername(String username) {
        return enrollmentRepository.findByStudentUsername(username);
    }

    /**
     * Retrieves all enrollments in the database.
     *
     * @return List of Enrollments.
     */
    public Iterable<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    /**
     * Create enrollment by username.
     *
     * @param username The username of the student.
     * @param course The course to enroll in.
     * @param teacherName The name of the teacher.
     * @param parallelId Id of parallel.
     */
    public void createEnrollmentByUsername(String username, String course, String teacherName, Long parallelId) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentUsername(username);
        enrollment.setCourse(course);
        enrollment.setTeacherName(teacherName);
        enrollment.setParallelId(parallelId);
        enrollmentRepository.save(enrollment);
    }

    /**
     * Grade student by username.
     *
     * @param studentUsername The username of the student.
     * @param enrollmentRequest The request body containing details to grade a student.
     * @throws EnrollmentException if no enrollment is found.
     * @throws IllegalArgumentException if grade does not match requested format.
     */
    public void gradeStudentByUsername(String studentUsername, EnrollmentRequest enrollmentRequest) throws EnrollmentException {
        Grade studentGrade;
        try {
            studentGrade = Grade.valueOf(enrollmentRequest.getGrade());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid grade format", e);
        }

        Enrollment enrollment = enrollmentRepository.findByStudentUsernameAndTeacherName(
                        studentUsername,
                        enrollmentRequest.getTeacherName())
                .stream()
                .findFirst()
                .orElseThrow(() -> new EnrollmentException("No matching enrollment found for student with specified teacher"));

        enrollment.setGrade(studentGrade);
        enrollmentRepository.save(enrollment);
    }

    /**
     * Remove enrollment by username and parallelId.
     *
     * @param username The username of the student.
     * @param parallelId Id of parallel.
     */
    public void deleteEnrollmentByUsername(String username, Long parallelId) throws EnrollmentException {
        Enrollment enrollment = enrollmentRepository.findByStudentUsernameAndParallelId(username, parallelId).orElseThrow(() -> new EnrollmentException("No matching enrollment found for student with specified parallel"));
        enrollmentRepository.delete(enrollment);
    }

}

