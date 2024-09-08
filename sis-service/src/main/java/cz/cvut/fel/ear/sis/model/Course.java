package cz.cvut.fel.ear.sis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference("teacher_courses")
    private Teacher teacher;
    @Column(nullable = false, unique = true, updatable = false)
    private String name;
    @Column(nullable = false, unique = true, updatable = false)
    private String code;
    @Column(nullable = false, updatable = false)
    private int ECTS;
    @Column(nullable = false, updatable = false)
    private Locale language;
    @OneToMany
    @JsonManagedReference("course_parallels")
    @JoinColumn(name = "course_id")
    private List<Parallel> parallelsList = new ArrayList<>();

    public void addParallel(Parallel parallel){
        this.parallelsList.add(parallel);
    }

    public void removeParallel(Parallel parallel){
        this.parallelsList.remove(parallel);
    }

}
