package cz.cvut.fel.ear.sis.service.interfaces;

import cz.cvut.fel.ear.sis.dto.EnrollmentDto;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.utils.exception.EnrollmentException;
import cz.cvut.fel.ear.sis.utils.exception.ParallelException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import cz.cvut.fel.ear.sis.utils.exception.StudentException;

import java.util.List;

public interface StudentService {
    List<Parallel> getAllParallelsForNextSemester() throws ParallelException, SemesterException;

    List<Parallel> getAllEnrolledParallelsForNextSemester(long studentId, String semesterCode) throws ParallelException;

    List<Parallel> getAllEnrolledParallelsForNextSemesterByStudentUsername(String username, String semesterCode);

    List<EnrollmentDto> getEnrollmentReportByUsername(String username) throws StudentException;

    void enrollToParallelByUsername(String username, Long parallelId) throws StudentException, ParallelException, EnrollmentException, SemesterException;

    Semester findNextSemester() throws SemesterException;

    void dropFromParallelByUsername(String username, Long parallelId) throws StudentException, ParallelException;

    void checkValidSemester(Parallel parallel, String username) throws SemesterException, EnrollmentException, ParallelException;

}
