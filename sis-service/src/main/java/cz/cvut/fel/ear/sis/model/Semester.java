package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Semester {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, updatable = false)
    private LocalDate startDate;
    @Column(nullable = false, updatable = false)
    private LocalDate endDate;
    @Column(nullable = false, updatable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private SemesterType semesterType;

    public Semester() {

    }

    public Semester(int year, SemesterType semesterType) {
        this.startDate = LocalDate.of(year, semesterType.getStartDate().getMonth(), semesterType.getStartDate().getDayOfMonth());
        this.endDate = LocalDate.of(year, semesterType.getEndDate().getMonth(), semesterType.getEndDate().getDayOfMonth());
        this.semesterType = semesterType;
        this.code = semesterType.name() + year;
        this.isActive = false;
    }
}
