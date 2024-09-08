package cz.cvut.fel.ear.sis.service.interfaces;

import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;

import java.time.LocalDate;

public interface PersonService {

    Person createANewPerson(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String password, String roleKeypass) throws PersonException;
}
