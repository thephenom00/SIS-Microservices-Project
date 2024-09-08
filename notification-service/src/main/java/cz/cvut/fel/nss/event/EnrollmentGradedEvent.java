package cz.cvut.fel.nss.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentGradedEvent {
    private String studentUsername;
    private String teacherFullName;
    private String course;
    private String grade;
}
