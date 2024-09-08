package cz.cvut.fel.ear.sis.dto;

import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParallelDto {
    private Long id;
    private String timeSlot;
    private DayOfWeek dayOfWeek;
    private String courseName;
    private String courseCode;
    private String teacherName;
    private String classroomCode;
}
