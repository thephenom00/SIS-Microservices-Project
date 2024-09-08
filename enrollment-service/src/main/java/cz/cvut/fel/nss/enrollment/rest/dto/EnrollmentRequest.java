package cz.cvut.fel.nss.enrollment.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EnrollmentRequest {
    private String course;
    private String teacherName;
    private String grade;
    private Long parallelId;
}
