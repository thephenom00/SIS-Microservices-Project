package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.dto.ClassroomDto;
import cz.cvut.fel.ear.sis.dto.SemesterDto;
import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.repository.ClassroomRepository;
import cz.cvut.fel.ear.sis.repository.SemesterRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.service.interfaces.AdminService;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.exception.ClassroomException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final SemesterRepository semesterRepository;
    private final ClassroomRepository classroomRepository;

    @Autowired
    public AdminServiceImpl(SemesterRepository semesterRepository, ClassroomRepository classroomRepository) {
        this.semesterRepository = semesterRepository;
        this.classroomRepository = classroomRepository;
    }

    /**
     * Creates a new Semester with the specified year and type.
     *
     * @param year         The year of the semester.
     * @param semesterType The type of the semester (e.g., FALL, SPRING).
     * @return The created Semester object.
     * @throws SemesterException If a semester with the same code already exists.
     */
    @Transactional
    public Semester createSemester(int year, SemesterType semesterType) throws SemesterException {
        if (semesterExists(semesterType.name() + year))
            throw new SemesterException("Semester already exists");
        Semester semester = new Semester(year, semesterType);
        semesterRepository.save(semester);
        return semester;
    }

    /**
     * Retrieves a Semester by its unique code.
     *
     * @param code The code of the Semester.
     * @return Optional containing the Semester object if found, otherwise empty.
     */
    @Transactional
    public Optional<Semester> getSemesterByCode(String code){
        return semesterRepository.findSemesterByCode(code);
    }

    /**
     * Checks if a Semester exists by its code.
     *
     * @param code The code of the Semester.
     * @return True if the Semester exists, otherwise false.
     */
    @Transactional
    public boolean semesterExists(String code){
        return getSemesterByCode(code).isPresent();
    }

    /**
     * Retrieves the active Semester.
     *
     * @return Optional containing the active Semester object if found, otherwise empty.
     */
    @Transactional
    public Optional<Semester> getActiveSemester(){
        return semesterRepository.findSemesterByIsActiveIsTrue();
    }

    /**
     * Sets a specified Semester as the active Semester.
     *
     * @param semester The Semester to set as active.
     */
    @Transactional
    public void setActiveSemester(Semester semester){
        Optional<Semester> activeSemester = getActiveSemester();
        boolean activeSemesterExists = activeSemester.isPresent();
        if (activeSemesterExists){
            Semester active = getActiveSemester().get();
            active.setIsActive(false);
            semesterRepository.save(active);
        }
        semester.setIsActive(true);
        semesterRepository.save(semester);
    }

    /**
     * Retrieves a list of all Semesters.
     *
     * @return List of Semester objects.
     */
    @Transactional
    public List<Semester> getAllSemesters(){
        return semesterRepository.findAll();
    }

    /**
     * Creates a new Classroom with the specified code and capacity.
     *
     * @param code     The code of the Classroom.
     * @param capacity The capacity of the Classroom.
     * @return The created Classroom object.
     * @throws ClassroomException If a Classroom with the same code already exists or capacity is invalid.
     */
    @Transactional
    public Classroom createClassroom(String code, int capacity) throws ClassroomException {
        if (classroomExists(code)) throw new ClassroomException("Classroom with such code already exists");
        if (capacity < 1 || capacity > 200) throw new ClassroomException("Classroom capacity must be 1-200.");
        Classroom classroom = Classroom.builder().code(code).capacity(capacity).build();
        classroomRepository.save(classroom);
        return classroom;
    }

    /**
     * Retrieves a Classroom by its unique code.
     *
     * @param code The code of the classroom.
     * @return Optional containing the Classroom object if found, otherwise empty.
     */
    @Transactional
    public Optional<Classroom> getClassroomByCode(String code){
        return classroomRepository.findClassroomByCode(code);
    }

    /**
     * Checks if a Classroom exists by its code.
     *
     * @param code The code of the classroom.
     * @return True if the Classroom exists, otherwise false.
     */
    @Transactional
    public boolean classroomExists(String code){
        return getClassroomByCode(code).isPresent();
    }

    /**
     * Retrieves a list of all classrooms in the system.
     *
     * @return List of Classroom objects.
     */
    @Transactional
    public List<Classroom> getAllClassrooms(){
        return classroomRepository.findAll();
    }

    /**
     * Converts a list of Semester entities to a list of Semester DTOs.
     *
     * @param semesters The list of semesters to map.
     * @return List of semesters DTOs.
     */
    public List<SemesterDto> mapSemestersToDto(List<Semester> semesters){
        return semesters.stream()
                .map(semester -> mapSemesterToDto(semester))
                .collect(Collectors.toList());
    }

    /**
     * Converts a Semester entity to a Semester DTO.
     *
     * @param semester Semester to map.
     * @return Semester DTO.
     */
    public SemesterDto mapSemesterToDto(Semester semester){
        return SemesterDto.builder()
                .id(semester.getId())
                .startDate(semester.getStartDate())
                .endDate(semester.getEndDate())
                .code(semester.getCode())
                .isActive(semester.getIsActive())
                .semesterType(semester.getSemesterType())
                .build();
    }

    /**
     * Converts a list of Classroom entities to a list of Classroom DTOs.
     *
     * @param classrooms The list of classrooms to map.
     * @return List of Classrooms DTOs.
     */
    public List<ClassroomDto> mapClassroomsToDto(List<Classroom> classrooms){
        return classrooms.stream()
                .map(classroom -> mapClassroomToDto(classroom))
                .collect(Collectors.toList());
    }

    /**
     * Converts a Classroom entity to a Classroom DTO.
     *
     * @param classroom Classroom to map.
     * @return Classroom DTO.
     */
    public ClassroomDto mapClassroomToDto(Classroom classroom){
        return ClassroomDto.builder()
                .id(classroom.getId())
                .code(classroom.getCode())
                .capacity(classroom.getCapacity())
                .build();
    }
}
