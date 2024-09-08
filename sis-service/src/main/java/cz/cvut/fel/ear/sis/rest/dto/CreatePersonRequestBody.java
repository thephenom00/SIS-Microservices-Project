package cz.cvut.fel.ear.sis.rest.dto;

import java.time.LocalDate;

public class CreatePersonRequestBody {
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public LocalDate birthDate;
    public String password;
    public String roleKeypass;
}
