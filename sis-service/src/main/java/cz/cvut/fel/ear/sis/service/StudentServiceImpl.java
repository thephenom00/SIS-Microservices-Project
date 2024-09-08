package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.dto.EnrollmentDto;
import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.rest.dto.EnrollmentRequest;
import cz.cvut.fel.ear.sis.service.interfaces.StudentService;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.exception.EnrollmentException;
import cz.cvut.fel.ear.sis.utils.exception.ParallelException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import cz.cvut.fel.ear.sis.utils.exception.StudentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final ParallelRepository parallelRepository;
    private final AdminServiceImpl adminServiceImpl;
    private final WebClient.Builder webClientBuilder;
    private final SemesterRepository semesterRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, ParallelRepository parallelRepository, AdminServiceImpl adminServiceImpl, WebClient.Builder webClientBuilder, SemesterRepository semesterRepository) {
        this.studentRepository = studentRepository;
        this.parallelRepository = parallelRepository;
        this.adminServiceImpl = adminServiceImpl;
        this.webClientBuilder = webClientBuilder;
        this.semesterRepository = semesterRepository;
    }

    /**
     * Retrieves a list of parallels for the next semester.
     *
     * @return List of Parallel objects for the next semester.
     * @throws SemesterException If parallels are not found for the next semester.
     */
    public List<Parallel> getAllParallelsForNextSemester() throws SemesterException {
        LocalDate nextSemesterStartDate = findNextSemester().getStartDate();
        return parallelRepository.findAllBySemesterStartDate(nextSemesterStartDate);
    }

    /**
     * Retrieves a list of all parallels for the current semester.
     *
     * @return List of Parallel objects for the current semester.
     * @throws SemesterException If parallels are not found for the current semester.
     */
    public List<Parallel> getAllParallelsForCurrentSemester() throws SemesterException {
        LocalDate currentSemesterStartDate = adminServiceImpl.getActiveSemester().orElseThrow(()-> new SemesterException("Active semester not found")).getStartDate();
        return parallelRepository.findAllBySemesterStartDate(currentSemesterStartDate);
    }

    /**
     * Retrieves a list of all parallels for the next semester.
     *
     * @return List of Parallel objects for the next semester.
     */
    public List<Parallel> getAllStudentsEnrolledParallels(String username) {
        return parallelRepository.findAllByStudentsUsername(username);
    }

    /**
     * Retrieves a list of enrolled parallels for a student in the next semester.
     *
     * @param studentId    The ID of the student.
     * @param semesterCode The code of the semester.
     * @return List of Parallel objects enrolled by the student in the next semester.
     */
    public List<Parallel> getAllEnrolledParallelsForNextSemester(long studentId, String semesterCode) {
        return parallelRepository.findAllByStudentIdAndSemesterCode(studentId,semesterCode);
    }

    /**
     * Retrieves a list of parallels for a course in the next semester.
     *
     * @param courseCode The code of the course.
     * @return List of Parallel objects for the course in the next semester.
     */
    public List<Parallel> getParallelsForCourseNextSemester(String courseCode) {
        return parallelRepository.findAllByCourseCode(courseCode);
    }

    /**
     * Retrieves a list of enrolled parallels for a student by username in the next semester.
     *
     * @param username     The username of the student.
     * @param semesterCode The code of the semester.
     * @return List of Parallel objects enrolled by the student in the next semester.
     */
    public List<Parallel> getAllEnrolledParallelsForNextSemesterByStudentUsername(String username,String semesterCode){
        return parallelRepository.findAllByStudentUsernameAndSemesterCode(username,semesterCode);
    }

    /**
     * Retrieves an enrollment report for a student by username.
     *
     * @param username The username of the student.
     * @return List of Enrollment objects for the student.
     */
    public List<EnrollmentDto> getEnrollmentReportByUsername(String username) {
        return webClientBuilder.build().get()
                .uri("http://enrollment-service:8081/enrollment/{username}", username)
                .retrieve()
                .bodyToFlux(EnrollmentDto.class)
                .collectList()
                .block();
    }

    /**
     * Enrolls a student in a parallel by username.
     *
     * @param username   The username of the student to enroll.
     * @param parallelId The ID of the parallel to enroll in.
     * @throws StudentException   If the student is not found.
     * @throws ParallelException  If the parallel is not found.
     * @throws EnrollmentException If enrollment details are not valid.
     */
    @Transactional
    public void enrollToParallelByUsername(String username, Long parallelId) throws StudentException, ParallelException, EnrollmentException, SemesterException {
        Student student = studentRepository.findByUserName(username).orElseThrow(()-> new StudentException("Student not found"));
        Parallel parallel = parallelRepository.findById(parallelId).orElseThrow(()-> new ParallelException("Parallel not found"));
        Course course = parallel.getCourse();

        checkValidSemester(parallel, username);

        Parallel enrolledParallel = parallelRepository.findByStudentUsernameAndCourseId(username, course.getId());

        if (enrolledParallel != null) {
            dropFromParallelByUsername(username, enrolledParallel.getId());
        }

        parallel.addStudent(student);
        parallelRepository.save(parallel);

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(course.getCode(), parallel.getTeacherFullName(), null, parallel.getId());

        webClientBuilder.build().post()
            .uri("http://enrollment-service:8081/enrollment/{username}", username)
            .bodyValue(enrollmentRequest)
            .header("Content-Type", "application/json")
            .retrieve()
            .toBodilessEntity()
            .block();
    }

    /**
     * Checks if the semester is valid for enrollment.
     *
     * @param parallel The parallel to check.
     * @param username The username of the student.
     * @throws SemesterException   If the active semester is not found.
     * @throws EnrollmentException If the semester is not valid for enrollment.
     * @throws ParallelException   If the parallel is full.
     */
    public void checkValidSemester(Parallel parallel, String username) throws SemesterException, EnrollmentException, ParallelException {
        Semester activeSemester = adminServiceImpl.getActiveSemester().orElseThrow(()-> new SemesterException("Active semester not found"));
        LocalDate semesterStartDate = activeSemester.getStartDate();
        LocalDate parallelStartDate = parallel.getSemester().getStartDate();

        if (parallelStartDate.isBefore(semesterStartDate) ||
                parallelStartDate.isAfter(semesterStartDate.plusYears(1)))
            throw new EnrollmentException("Students can enroll only for the next semester");

        if (parallel.getStudents().size() >= parallel.getCapacity())
            throw new ParallelException("Parallel is full");

        if (parallel.getStudents().stream().anyMatch(student -> student.getUserName().equals(username))){
            throw new EnrollmentException("Student is already enrolled in this parallel");
        }
    }

    /**
     * Finds the next semester.
     * @return The next semester.
     * @throws SemesterException If the next semester is not found.
     */
    public Semester findNextSemester() throws SemesterException {
        Semester activeSemester = semesterRepository.findSemesterByIsActiveIsTrue().orElseThrow(()-> new SemesterException("Active semester not found"));
        Semester nextSemester;

        if(activeSemester.getSemesterType().equals(SemesterType.SPRING))
            nextSemester = semesterRepository.findSemesterByCode("FALL"+activeSemester.getStartDate().getYear()).orElseThrow(()-> new SemesterException("Next semester not found, tried to find FALL"+activeSemester.getStartDate().getYear()));
        else
            nextSemester = semesterRepository.findSemesterByCode("SPRING"+(activeSemester.getStartDate().getYear()+1)).orElseThrow(()-> new SemesterException("Next semester not found, tried to find SPRING"+activeSemester.getStartDate().getYear()+1));

        return nextSemester;
    }

    /**
     * Drops a student from a parallel by username.
     *
     * @param username   The username of the student to drop.
     * @param parallelId The ID of the parallel to drop from.
     * @throws StudentException  If the student is not found.
     * @throws ParallelException If the parallel is not found.
     */
    @Transactional
    public void dropFromParallelByUsername(String username, Long parallelId) throws StudentException, ParallelException {
        Student student = studentRepository.findByUserName(username).orElseThrow(() -> new StudentException("Student not found"));
        Parallel parallel = parallelRepository.findById(parallelId).orElseThrow(() -> new ParallelException("Parallel not found"));

        if (!parallel.getStudents().contains(student)) {
            throw new StudentException("Student is not enrolled in this parallel");
        }

        parallel.removeStudent(student);
        parallelRepository.save(parallel);

        webClientBuilder.build().delete()
                .uri("http://enrollment-service:8081/enrollment/{username}/{parallelId}", username, parallel.getId())
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
