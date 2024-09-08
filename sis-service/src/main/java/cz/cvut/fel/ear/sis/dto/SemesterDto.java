package cz.cvut.fel.ear.sis.dto;

import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class SemesterDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String code;
    private Boolean isActive;
    private SemesterType semesterType;
}
