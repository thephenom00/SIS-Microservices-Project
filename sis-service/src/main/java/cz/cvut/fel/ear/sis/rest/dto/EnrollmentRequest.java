package cz.cvut.fel.ear.sis.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EnrollmentRequest {

    String course;

    String teacherName;

    String grade;

    Long parallelId;
}
