package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends Person {

    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String userName, String password) {
        super(firstName, lastName, email, phoneNumber, birthDate, userName, password, "ROLE_STUDENT");
    }

}
