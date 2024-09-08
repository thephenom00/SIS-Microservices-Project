package cz.cvut.fel.ear.sis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Parallel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, updatable = false)
    private int capacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TimeSlot timeSlot;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private DayOfWeek dayOfWeek;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    private Semester semester;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    private Classroom classroom;

    @JsonBackReference("course_parallels")
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany
    @JoinTable(name = "parallel_student",
            joinColumns = @JoinColumn(name = "parallel_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students = new ArrayList<>();


    public void addStudent(Student student){
        students.add(student);
    }

    public void removeStudent(Student student){
        students.remove(student);
    }

    public String getTeacherFullName() {
        return this.getCourse().getTeacher().getFirstName() + " " + this.getCourse().getTeacher().getLastName();
    }

    public String formatTimeSlot() {
        return this.getTimeSlot().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + this.getTimeSlot().getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
