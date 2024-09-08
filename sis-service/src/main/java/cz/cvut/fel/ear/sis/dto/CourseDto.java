package cz.cvut.fel.ear.sis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private String name;
    private String code;
    private int ECTS;
    private List<String> parallelsList;
}
