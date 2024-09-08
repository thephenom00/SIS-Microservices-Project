package cz.cvut.fel.ear.sis.rest;

import cz.cvut.fel.ear.sis.dto.ClassroomDto;
import cz.cvut.fel.ear.sis.dto.SemesterDto;
import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.rest.dto.CreateClassroomRequestBody;
import cz.cvut.fel.ear.sis.rest.dto.CreateSemesterRequestBody;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.service.AdminServiceImpl;
import cz.cvut.fel.ear.sis.utils.exception.ClassroomException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/admin")
public class AdminController {
    private final AdminServiceImpl adminServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public AdminController(AdminServiceImpl adminServiceImpl){
        this.adminServiceImpl = adminServiceImpl;
    }

    /**
     * Retrieves all classrooms.
     *
     * @return A ResponseEntity containing a list of ClassroomDto objects.
     * @throws ClassroomException If no classrooms are found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/room", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ClassroomDto>> getClassrooms() throws ClassroomException {
        List<Classroom> classrooms = adminServiceImpl.getAllClassrooms();

        if (classrooms.isEmpty()) {
            throw new ClassroomException("No classrooms found.");
        }

        List<ClassroomDto> classroomDtos = adminServiceImpl.mapClassroomsToDto(classrooms);

        return ResponseEntity.ok(classroomDtos);
    }

    /**
     * Creates a new classroom.
     *
     * @param body The request body containing the classroom details.
     * @return A ResponseEntity with a location header indicating the successful creation of the classroom.
     * @throws ClassroomException If there is an issue creating the classroom.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/classroom", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> createClassroom(@RequestBody CreateClassroomRequestBody body) throws ClassroomException {
        Classroom classroom = adminServiceImpl.createClassroom(body.code, body.capacity);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", classroom.getCode());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Retrieves a classroom by its code.
     *
     * @param code The unique code of the classroom.
     * @return A ResponseEntity containing the ClassroomDto of the requested classroom.
     * @throws ClassroomException If the classroom with the provided code is not found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/classroom/{code}")
    public ResponseEntity<ClassroomDto> getClassroomByCode(@PathVariable String code) throws ClassroomException {
        final Optional<Classroom> classroom = adminServiceImpl.getClassroomByCode(code);

        if (classroom.isEmpty()) {
            throw new ClassroomException("Classroom not found.");
        }

        ClassroomDto classroomDto = adminServiceImpl.mapClassroomToDto(classroom.get());

        return ResponseEntity.ok(classroomDto);
    }

    /**
     * Retrieves all semesters.
     *
     * @return A ResponseEntity containing a list of SemesterDto objects.
     * @throws SemesterException If no semesters are found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/semester", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SemesterDto>> getSemesters() throws SemesterException {
        List<Semester> semesters = adminServiceImpl.getAllSemesters();
        List<SemesterDto> semesterDtos = adminServiceImpl.mapSemestersToDto(semesters);

        if(semesters.isEmpty()){
            throw new SemesterException("No semesters found.");
        }

        return ResponseEntity.ok(semesterDtos);
    }

    /**
     * Creates a new semester.
     *
     * @param body The request body containing the semester details.
     * @param auth The authentication object containing user details.
     * @return A ResponseEntity with a location header indicating the successful creation of the semester.
     * @throws SemesterException If there is an issue creating the semester.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/semester", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> createSemester(@RequestBody CreateSemesterRequestBody body,Authentication auth) throws SemesterException {
        logger.info("User: " + auth.getName());
        logger.info("Authorities: " + auth.getAuthorities());

        Semester semester = adminServiceImpl.createSemester(body.year, body.semesterType);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", semester.getCode());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Retrieves a semester by its code.
     *
     * @param code The unique code of the semester.
     * @return A ResponseEntity containing the SemesterDto of the requested semester.
     * @throws SemesterException If the semester with the provided code is not found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/semester/{code}")
    public ResponseEntity<SemesterDto> getSemesterByCode(@PathVariable String code) throws SemesterException {
        final Optional<Semester> semester = adminServiceImpl.getSemesterByCode(code);
        if(semester.isEmpty()){
            throw new SemesterException("Semester not found.");
        }
    return ResponseEntity.ok(adminServiceImpl.mapSemesterToDto(semester.get()));
    }

    /**
     * Sets a semester as active based on its code.
     *
     * @param code The unique code of the semester to be set as active.
     * @return A ResponseEntity with a location header indicating the operation success.
     * @throws SemesterException If the semester with the provided code is not found.
     */
    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "semester/{code}")
    public ResponseEntity<HttpStatus> setActiveSemester(@PathVariable String code) throws SemesterException {
        Optional<Semester> semester = adminServiceImpl.getSemesterByCode(code);
        if(semester.isEmpty()){
            throw new SemesterException("Semester not found.");
        }
        adminServiceImpl.setActiveSemester(semester.get());
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }

    /**
     * Retrieves all semesters.
     *
     * @return A ResponseEntity containing a list of all SemesterDto objects.
     * @throws SemesterException If no semesters are found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/semester/all")
    public ResponseEntity<List<SemesterDto>> getAllSemesters() throws SemesterException {
        List<Semester> semesters = adminServiceImpl.getAllSemesters();
        if(semesters.isEmpty()){
            throw new SemesterException("No semesters found.");
        }
        List<SemesterDto> semesterDtos = adminServiceImpl.mapSemestersToDto(semesters);
        return ResponseEntity.ok(semesterDtos);
    }

    /**
     * Retrieves the active semester.
     *
     * @return A ResponseEntity containing the active SemesterDto.
     * @throws SemesterException If there is no active semester found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/semester/active")
    public ResponseEntity<SemesterDto> getActiveSemester() throws SemesterException {
        Optional<Semester> semester = adminServiceImpl.getActiveSemester();
        if(semester.isEmpty()){
            throw new SemesterException("No active semester found.");
        }
        return ResponseEntity.ok(adminServiceImpl.mapSemesterToDto(semester.get()));
    }

}
