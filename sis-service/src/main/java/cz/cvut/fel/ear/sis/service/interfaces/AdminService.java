package cz.cvut.fel.ear.sis.service.interfaces;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.exception.ClassroomException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    Semester createSemester(int year, SemesterType semesterType) throws SemesterException;

    Optional<Semester> getSemesterByCode(String code);

    boolean semesterExists(String code);

    Optional<Semester> getActiveSemester();

    void setActiveSemester(Semester semester);

    List<Semester> getAllSemesters();

    Classroom createClassroom(String code, int capacity) throws ClassroomException;

    Optional<Classroom> getClassroomByCode(String code);

    boolean classroomExists(String code);

    List<Classroom> getAllClassrooms();

}
