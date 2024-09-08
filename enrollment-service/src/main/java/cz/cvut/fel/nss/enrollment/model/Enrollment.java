package cz.cvut.fel.nss.enrollment.model;

import cz.cvut.fel.nss.enrollment.utils.enums.Grade;
import cz.cvut.fel.nss.enrollment.utils.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Data
@Document(indexName = "enrollment")
public class Enrollment {
    @Id
    @GeneratedValue
    private String id;

    private String course;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String teacherName;

    private String studentUsername;

    private Long parallelId;

    public Enrollment() {
        this.status = Status.IN_PROGRESS;
    }

    public void setGrade(Grade grade) {
        if (grade==Grade.F){
            setStatus(Status.FAILED);
        } else {
            setStatus(Status.PASSED);
        }
        this.grade = grade;
    }

}
