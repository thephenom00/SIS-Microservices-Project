package cz.cvut.fel.nss.enrollment.repository;

import cz.cvut.fel.nss.enrollment.model.Enrollment;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends ElasticsearchRepository<Enrollment, Long> {
    List<Enrollment> findByStudentUsername(String username);
    @Query("{\"bool\": {\"must\": [{\"match\": {\"studentUsername\": \"?0\"}}, {\"match\": {\"teacherName\": \"?1\"}}]}}")
    List<Enrollment> findByStudentUsernameAndTeacherName(String studentUsername, String teacherName);
    @Query("{\"bool\": {\"must\": [{\"match\": {\"studentUsername\": \"?0\"}}, {\"match\": {\"parallelId\": \"?1\"}}]}}")
    Optional<Enrollment> findByStudentUsernameAndParallelId(String studentUsername, Long parallelId);
}
