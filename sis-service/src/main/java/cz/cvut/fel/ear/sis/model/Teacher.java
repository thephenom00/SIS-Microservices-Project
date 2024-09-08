package cz.cvut.fel.ear.sis.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TEACHER")
@Getter
@Setter
public class Teacher extends Person {

    @JsonManagedReference("teacher_courses")
    @OneToMany
    @JoinColumn(name = "teacher_id")
    @OrderBy("name ASC")
    private List<Course> myCourses = new ArrayList<>();

    public Teacher() {
        super();
    }

    public Teacher(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String userName, String password) {
        super(firstName, lastName, email, phoneNumber, birthDate, userName, password,"ROLE_TEACHER");
    }

    public void addCourse(Course course) {
        this.myCourses.add(course);
    }
    public void removeCourse(Course course) {
        this.myCourses.remove(course);
    }
}
