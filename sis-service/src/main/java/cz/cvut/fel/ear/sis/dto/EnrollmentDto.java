package cz.cvut.fel.ear.sis.dto;

import cz.cvut.fel.ear.sis.utils.enums.Grade;
import cz.cvut.fel.ear.sis.utils.enums.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentDto {
    private String course;
    private Grade grade;
    private Status status;
    private String teacherName;
}
