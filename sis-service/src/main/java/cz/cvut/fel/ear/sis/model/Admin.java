package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Person {

    public Admin() {
        super();
    }

    public Admin(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String userName, String password) {
        super(firstName, lastName, email, phoneNumber, birthDate, userName, password, "ROLE_ADMIN");
    }

}
