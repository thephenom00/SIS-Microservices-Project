package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.dto.CourseDto;
import cz.cvut.fel.ear.sis.dto.ParallelDto;
import cz.cvut.fel.ear.sis.dto.PersonDto;
import cz.cvut.fel.ear.sis.event.EnrollmentGradedEvent;
import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.rest.dto.EnrollmentRequest;
import cz.cvut.fel.ear.sis.service.interfaces.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.*;
import cz.cvut.fel.ear.sis.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.cvut.fel.ear.sis.utils.ServiceUtil.doesNotConformRegex;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final AdminServiceImpl adminServiceImpl;
    private final ParallelRepository parallelRepository;
    private final SemesterRepository semesterRepository;
    private final ClassroomRepository classroomRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, EnrollmentGradedEvent> kafkaTemplate;

    @Autowired
    public TeacherServiceImpl(StudentRepository studentRepository, TeacherRepository teacherRepository, CourseRepository courseRepository, AdminServiceImpl adminServiceImpl, ParallelRepository parallelRepository, SemesterRepository semesterRepository, ClassroomRepository classroomRepository, WebClient.Builder webClientBuilder, KafkaTemplate kafkaTemplate) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.adminServiceImpl = adminServiceImpl;
        this.parallelRepository = parallelRepository;
        this.semesterRepository = semesterRepository;
        this.classroomRepository = classroomRepository;
        this.webClientBuilder = webClientBuilder;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Creates a new Course with the provided details.
     *
     * @param teacherId   The ID of the teacher creating the course.
     * @param courseName  The name of the course.
     * @param code        The code of the course.
     * @param ECTS        The ECTS credits for the course.
     * @param language    The language of the course (either "CZ" or "EN").
     * @return The created Course object.
     * @throws CourseException  If course details are not valid.
     * @throws PersonException  If the teacher is not found or not valid.
     */
    @Transactional
    public Course createCourse(long teacherId, String courseName, String code, int ECTS,Locale language) throws CourseException, PersonException {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(()-> new PersonException("Teacher not found"));
        checkValidCourse(teacher, courseName, code, ECTS, language);
        Course course = Course.builder().teacher(teacher).name(courseName).code(code).ECTS(ECTS).language(language).build();
        courseRepository.save(course);
        teacherRepository.save(teacher);
        return course;
    }

    /**
     * Converts a list of courses entities to a list of course DTOs.
     *
     * @param courses The list of courses to map.
     * @return List of courses DTOs.
     */
    public List<CourseDto> mapCoursesToDto(List<Course> courses){
        List<CourseDto> coursesDto = new ArrayList<>();
        for (Course course : courses) {
            List<String> parallelsList = new ArrayList<>();
            for (Parallel parallel : course.getParallelsList()) {
                parallelsList.add(parallel.getId().toString());
            }
            coursesDto.add(new CourseDto(course.getName(), course.getCode(), course.getECTS(), parallelsList));
        }
        return coursesDto;
    }

    /**
     * Converts a list of parallels to a list of parallels DTOs.
     *
     * @param parallels The list of parallels to map.
     * @return List of parallels DTOs.
     */
    public List<ParallelDto> mapParallelsToDto(List<Parallel> parallels){
        return parallels.stream()
                .map(parallel -> ParallelDto.builder()
                        .id(parallel.getId())
                        .timeSlot(parallel.formatTimeSlot())
                        .dayOfWeek(parallel.getDayOfWeek())
                        .courseName(parallel.getCourse().getName())
                        .courseCode(parallel.getCourse().getCode())
                        .teacherName(parallel.getTeacherFullName())
                        .classroomCode(parallel.getClassroom().getCode())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of persons to a list of persons DTOs.
     *
     * @param students The list of persons to map.
     * @return List of persons DTOs.
     */
    public List<PersonDto> mapStudentsToDto(List<Student> students){
        return students.stream()
                .map(person -> PersonDto.builder()
                        .id(person.getId())
                        .firstName(person.getFirstName())
                        .lastName(person.getLastName())
                        .userName(person.getUserName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Validates the course details based on the provided parameters.
     *
     * @param teacher     The teacher object.
     * @param courseName  The name of the course.
     * @param code        The code of the course.
     * @param ECTS        The ECTS credits for the course.
     * @param language    The language of the course.
     * @throws CourseException  If course details are not valid.
     * @throws PersonException  If the teacher is not valid.
     */
    private void checkValidCourse(Teacher teacher, String courseName, String code, int ECTS, Locale language) throws CourseException, PersonException {
        if (teacher == null) {
            throw new PersonException("Teacher is not valid");
        }
        if (courseName == null || doesNotConformRegex(courseName, "^[a-zA-ZáčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ0-9\\s.,!?()-]{3,50}$")) {
            throw new CourseException("Course name is not valid");
        }

        if (code == null || code.length() > 10 || code.length()<3) {
            throw new CourseException("Course code is not valid");
        }

        if (ECTS < 0 || ECTS > 30) {
            throw new CourseException("ECTS is not valid");
        }
        if (language == null || (!language.equals(Locale.ENGLISH) && !language.equals(Locale.forLanguageTag("CZ")))   ) {
            throw new CourseException("Language is not valid");
        }

    }

    /**
     * Creates a new parallel with the provided details.
     *
     * @param teacherId   The ID of the teacher creating the parallel.
     * @param capacity    The capacity of the parallel.
     * @param timeSlot    The time slot for the parallel.
     * @param dayOfWeek   The day of the week for the parallel.
     * @param semesterId  The ID of the semester for the parallel.
     * @param classroomId The ID of the classroom for the parallel.
     * @param courseId    The ID of the course for the parallel.
     * @return The created Parallel object.
     * @throws CourseException    If course details are not valid.
     * @throws ParallelException  If parallel details are not valid.
     * @throws ClassroomException If the classroom is not found.
     * @throws SemesterException  If the semester is not found.
     */
    @Transactional
    public Parallel createParallel(long teacherId, int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek, long semesterId, long classroomId, long courseId) throws CourseException, ParallelException, ClassroomException, SemesterException {
        Semester semester = semesterRepository.findById(semesterId).orElseThrow(()-> new SemesterException("Semester not found"));
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(()-> new ClassroomException("Classroom not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseException("Course not found"));

        checkValidParallel(capacity, timeSlot, dayOfWeek, semester, classroom);

        Parallel parallel = Parallel.builder().capacity(capacity).timeSlot(timeSlot).dayOfWeek(dayOfWeek).semester(semester).classroom(classroom).course(course).build();

        course.addParallel(parallel);
        parallelRepository.save(parallel);
        courseRepository.save(course);

        return parallel;
    }

    /**
     * Validates the parallel details based on the provided parameters.
     *
     * @param capacity  The capacity of the parallel.
     * @param timeSlot  The time slot for the parallel.
     * @param dayOfWeek The day of the week for the parallel.
     * @param semester  The semester for the parallel.
     * @param classroom The classroom for the parallel.
     * @throws ParallelException  If parallel details are not valid.
     * @throws ClassroomException If the classroom already has an occupied time slot.
     * @throws SemesterException  If the semester date is not valid.
     */
    private boolean checkValidParallel(int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek, Semester semester, Classroom classroom) throws ParallelException, ClassroomException, SemesterException {

        if (capacity <= 0 || capacity > classroom.getCapacity()){
            throw new ParallelException("Capacity is not valid");
        }

        Semester activeSemester = adminServiceImpl.getActiveSemester().orElseThrow(()-> new SemesterException("Active semester not found"));

        if (semester.getStartDate().isAfter(activeSemester.getStartDate().plusYears(1)))
            throw new SemesterException("You can only make a parallel 2 semesters in advance of active semester");

        List<Parallel> sameTimeSlotParallels = parallelRepository.findByClassroomAndSemesterAndDayOfWeekAndTimeSlot(classroom, semester,dayOfWeek, timeSlot);

        if (!sameTimeSlotParallels.isEmpty()) {
            throw new ClassroomException("That classroom already has a parallel with this timeslot occupied");
        }

        return true;
    }

    /**
     * Updates the details of an existing course.
     *
     * @param courseId   The ID of the course to update.
     * @param teacherId  The ID of the teacher updating the course.
     * @param courseName The new name of the course.
     * @param code       The new code of the course.
     * @param ECTS       The new ECTS credits for the course.
     * @param language   The new language of the course.
     * @throws CourseException If course details are not valid.
     * @throws PersonException If the teacher is not found or not valid.
     */
    @Transactional
    public void updateCourse(long courseId,
                             long teacherId,
                             String courseName,
                             String code,
                             int ECTS,
                             Locale language) throws CourseException, PersonException {
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseException("Course not found"));
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(()-> new PersonException("Teacher not found"));

        checkValidCourse(teacher, courseName, code, ECTS, language);

        Course oldCourse = teacher.getMyCourses().get(0);
        course.setTeacher(teacher);
        course.setName(courseName);
        course.setCode(code);
        course.setECTS(ECTS);
        course.setLanguage(language);


        teacher.removeCourse(oldCourse);
        teacher.addCourse(course);

        courseRepository.save(course);
        teacherRepository.save(teacher);
    }

    /**
     * Grades a student based on the provided enrollment ID and grade.
     *
     * @throws StudentException    If the student is not found or not valid.
     */
    @Transactional
    public void gradeStudent(String studentUsername, EnrollmentRequest enrollmentRequest, Teacher teacher) throws StudentException {
        studentRepository.findByUserName(studentUsername).orElseThrow(()-> new StudentException("Student not found"));
        String teacherFullName = teacher.getFirstName() + " " + teacher.getLastName();

        enrollmentRequest.setTeacherName(teacherFullName);

        webClientBuilder.build().post()
                .uri("http://enrollment-service:8081/enrollment/grade/{studentUsername}", studentUsername)
                .bodyValue(enrollmentRequest)
                .header("Content-Type", "application/json")
                .retrieve()
                .toBodilessEntity()
                .block();

        EnrollmentGradedEvent enrollmentGradedEvent = new EnrollmentGradedEvent(studentUsername, teacherFullName, teacher.getMyCourses().get(0).getName(), enrollmentRequest.getGrade());
        kafkaTemplate.send("notificationTopic", enrollmentGradedEvent);
    }

    /**
     * Updates the details of an existing parallel.
     *
     * @param parallel   The parallel object to update.
     * @param teacher    The teacher object.
     * @param capacity   The new capacity of the parallel.
     * @param timeSlot   The new time slot for the parallel.
     * @param dayOfWeek  The new day of the week for the parallel.
     * @param semester   The new semester for the parallel.
     * @param classroom  The new classroom for the parallel.
     * @param course     The new course for the parallel.
     * @throws ParallelException  If parallel details are not valid.
     * @throws SemesterException  If the semester date is not valid.
     * @throws ClassroomException If the classroom already has an occupied time slot.
     */
    @Transactional
    public void updateParallel(Parallel parallel,
                               Teacher teacher,
                               int capacity,
                               TimeSlot timeSlot,
                               DayOfWeek dayOfWeek,
                               Semester semester,
                               Classroom classroom,
                               Course course) throws ParallelException, SemesterException, ClassroomException {

        if (checkValidParallel(capacity, timeSlot, dayOfWeek, semester, classroom)) {
            Course oldCourse = parallel.getCourse();
            oldCourse.removeParallel(parallel);

            parallel.setCapacity(capacity);
            parallel.setTimeSlot(timeSlot);
            parallel.setDayOfWeek(dayOfWeek);
            parallel.setSemester(semester);
            parallel.setClassroom(classroom);
            parallel.setCourse(course);


            course.addParallel(parallel);

            parallelRepository.save(parallel);
            courseRepository.save(oldCourse);
            courseRepository.save(course);
        }
    }

    /**
     * Retrieves all courses.
     *
     * @return List of all courses.
     */
    @Transactional(readOnly = true)
    public List<Course> getAllCoruses(){
        return courseRepository.findAll();
    }

    /**
     * Retrieves all parallels.
     *
     * @return List of all parallels.
     */
    @Transactional(readOnly = true)
    public List<Parallel> getAllParallels(){
        return parallelRepository.findAll();
    }

    @Override
    public List<Parallel> getNextSemesterTeacherParallels(long teacherId) {
        return null;
    }

    /**
     * Retrieves all teachers.
     *
     * @return List of all teachers.
     */
    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers(){
        return teacherRepository.findAll();
    }

    /**
     * Retrieves a teacher by ID.
     *
     * @param id The ID of the teacher.
     * @return Optional containing the teacher, or empty if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Teacher> getTeacherById(Long id){
        return teacherRepository.findById(id);
    }

    /**
     * Retrieves a course by ID.
     *
     * @param id The ID of the course.
     * @return Optional containing the course, or empty if not found.
     */

    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id){
        return courseRepository.findById(id);
    }

    /**
     * Retrieves courses by a teacher's ID.
     *
     * @param id The ID of the teacher.
     * @return List of courses associated with the teacher.
     */
    @Transactional(readOnly = true)
    public List<Course> getCourseByTeacherId(Long id){
        return courseRepository.findAllByTeacher_Id(id);
    }

    /**
     * Retrieves a parallel by ID.
     *
     * @param id The ID of the parallel.
     * @return Optional containing the parallel, or empty if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Parallel> getParallelById(Long id){
        return parallelRepository.findById(id);
    }

    /**
     * Retrieves parallels by a course's ID.
     *
     * @param id The ID of the course.
     * @return List of parallels associated with the course.
     */
    @Transactional(readOnly = true)
    public List<Parallel> getParallelByCourseId(Long id){
        return parallelRepository.findAllByCourseId(id);
    }

    /**
     * Retrieves all students by a parallel's ID.
     *
     * @param id The ID of the parallel.
     * @return List of students associated with the parallel.
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudentsByParallelId(Long id) throws ParallelException {
        return parallelRepository.findById(id).orElseThrow(()-> new ParallelException("Parallel not found")).getStudents();
    }

    /**
     * Retrieves courses by a teacher's username.
     *
     * @param username The username of the teacher.
     * @return List of courses associated with the teacher's username.
     */
    @Transactional(readOnly = true)
    public List<Course> getCoursesByTeacherUsername(String username){
        List<Course> courses = courseRepository.findCoursesByTeacher(username);
        System.out.println(courses);
        return courseRepository.findCoursesByTeacher(username);
    }

    /**
     * Retrieves a teacher by username.
     *
     * @param username The username of the teacher.
     * @return The teacher associated with the given username.
     */
    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUserName(username);
    }


}
