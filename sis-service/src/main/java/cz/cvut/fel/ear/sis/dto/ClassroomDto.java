package cz.cvut.fel.ear.sis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClassroomDto {
    private Long id;
    private String code;
    private int capacity;

}
