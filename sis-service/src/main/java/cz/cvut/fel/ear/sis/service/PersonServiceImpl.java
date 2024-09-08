package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.AdminRepository;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.repository.TeacherRepository;
import cz.cvut.fel.ear.sis.service.interfaces.PersonService;
import cz.cvut.fel.ear.sis.utils.enums.Role;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static cz.cvut.fel.ear.sis.utils.ServiceUtil.doesNotConformRegex;

@Service
public class PersonServiceImpl implements PersonService {

    private final AdminRepository adminRepository;
    private final PersonRepository personRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public PersonServiceImpl(AdminRepository adminRepository, PersonRepository personRepository, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.adminRepository = adminRepository;
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Creates a new person with the provided details based on the role specified.
     *
     * @param firstName   The first name of the person.
     * @param lastName    The last name of the person.
     * @param email       The email address of the person.
     * @param phoneNumber The phone number of the person.
     * @param birthDate   The birth date of the person.
     * @param password    The password for the person's account.
     * @param roleKeypass The key pass to determine the role of the person.
     * @return The created person object.
     * @throws PersonException If any validation fails or the key pass is invalid.
     */
    @Transactional
    public Person createANewPerson(String firstName,
                                   String lastName,
                                   String email,
                                   String phoneNumber,
                                   LocalDate birthDate,
                                   String password,
                                   String roleKeypass) throws PersonException {
        checkThatDetailsAreValid(firstName, lastName, email, phoneNumber, birthDate, password);
        String userName = generateUniqueUserName(firstName.toLowerCase(), lastName.toLowerCase());
        Person person = switch (roleKeypass) {
            case "studentKeyPass" ->
                    new Student(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            case "teacherKeyPass" ->
                    new Teacher(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            case "adminKeyPass" ->
                    new Admin(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            default -> throw new PersonException("KeyPass is not valid");
        };

        personRepository.save(person);
        return person;
    }

    /**
     * Updates the contact details (email and phone number) of a person with the specified ID.
     *
     * @param id             The ID of the person to update.
     * @param newEmail       The new email address for the person.
     * @param newPhoneNumber The new phone number for the person.
     * @throws PersonException If the person is not found or the details are invalid.
     */
    @Transactional
    public void updateContactDetails(Long id, String newEmail, String newPhoneNumber) throws PersonException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));
        checkThatContactDetailsAreValid(newEmail, newPhoneNumber);
        person.setEmail(newEmail);
        person.setPhoneNumber(newPhoneNumber);
        personRepository.save(person);
    }

    /**
     * Updates the name and username of a person with the specified ID.
     *
     * @param id        The ID of the person to update.
     * @param firstName The new first name for the person.
     * @param lastName  The new last name for the person.
     * @throws PersonException If the person is not found or the names are invalid.
     */
    @Transactional
    public void updateNameAndUsername(Long id, String firstName, String lastName) throws PersonException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));
        checkThatNameIsValid(firstName, lastName);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setUserName(generateUniqueUserName(firstName, lastName));
        personRepository.save(person);
    }

    /**
     * Retrieves a list of all people in the system.
     *
     * @return List of Person objects.
     */
    @Transactional(readOnly = true)
    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    /**
     * Retrieves a list of all admins in the system.
     *
     * @return List of Admin objects.
     */
    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    /**
     * Retrieves a list of all students in the system.
     *
     * @return List of Student objects.
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Retrieves a list of all teachers in the system.
     *
     * @return List of Teacher objects.
     */
    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    /**
     * Retrieves a person by their ID.
     *
     * @param id The ID of the person to retrieve.
     * @return Optional containing the Person object if found, otherwise empty.
     */
    @Transactional(readOnly = true)
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    /**
     * Retrieves the role of a person by their ID.
     *
     * @param id The ID of the person.
     * @return Role of the person.
     * @throws EntityNotFoundException If the person is not found.
     */
    @Transactional(readOnly = true)
    public Role getPersonRoleById(Long id) {
        if (adminRepository.existsById(id)) {
            return Role.ADMIN;
        } else if (studentRepository.existsById(id)) {
            return Role.STUDENT;
        } else if (teacherRepository.existsById(id)) {
            return Role.TEACHER;
        }
        throw new EntityNotFoundException();
    }

    /**
     * Generates a unique username based on the provided first and last names.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @return A unique username.
     */
    private String generateUniqueUserName(String firstName, String lastName) {
        firstName = firstName.replace(" ", "");
        lastName = lastName.replace(" ", "");
        String username;

        if (lastName.length() < 5) {
            username = lastName;
            if (firstName.length() < (8 - username.length())) {
                username += firstName;
                username += generateRandomNDigitNumber(8 - username.length());
            } else {
                username += firstName.substring(0, 8 - username.length());
            }
        } else {
            username = lastName.substring(0, 5);
            if (firstName.length() < 3) {
                username += firstName;
                username += generateRandomNDigitNumber(3 - username.length());
            } else {
                username += firstName.substring(0, 3);
            }
        }

        if (personRepository.existsByUserName(username)) {
            username = modifyUsername(username);
        }

        return username;
    }

    /**
     * Modifies the username to ensure its uniqueness.
     *
     * @param username The base username to modify.
     * @return A unique username.
     */
    private String modifyUsername(String username) {
        String baseUsername = username.substring(0, username.length() - 1);
        char lastChar = username.charAt(username.length() - 1);

        if (Character.isLetter(lastChar)) {
            baseUsername += generateRandomNDigitNumber(1);
        } else {
            int num = Character.getNumericValue(lastChar) + 1;
            baseUsername += num;
        }

        if (personRepository.existsByUserName(baseUsername)) {
            return modifyUsername(baseUsername);
        }

        return baseUsername;
    }

    /**
     * Generates a random n-digit number.
     *
     * @param n The number of digits.
     * @return A random n-digit number.
     */
    private int generateRandomNDigitNumber(int n) {
        Random random = new Random();
        return random.nextInt(9 * ((int) Math.pow(10, n - 1)));
    }

    /**
     * Checks that the provided details are valid.
     * Validates the first and last names, email, phone number, birthdate, and password.
     *
     * @param firstName   The first name of the user.
     * @param lastName    The last name of the user.
     * @param email       The email of the user.
     * @param phoneNumber The phone number of the user.
     * @param birthDate   The birthdate of the user.
     * @param password    The password of the user.
     * @throws PersonException If any of the details are invalid.
     */
    private void checkThatDetailsAreValid(String firstName, String lastName, String email,
                                         String phoneNumber, LocalDate birthDate, String password) throws PersonException {
        checkThatNameIsValid(firstName, lastName);
        checkThatContactDetailsAreValid(email, phoneNumber);

        if (Period.between(birthDate, LocalDate.now()).getYears() < 18)
            throw new PersonException("Only users 18 years old and older can sign up.");
        if (doesNotConformRegex(password, "^[A-Za-z0-9]{1,20}$"))
            throw new PersonException("Password is not valid.");
    }

    /**
     * Checks that the provided first and last names are valid.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @throws PersonException If either the first name or last name is invalid.
     */
    private void checkThatNameIsValid(String firstName, String lastName) throws PersonException{
        if (doesNotConformRegex(firstName, "[a-zA-ZáčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ ]++"))
            throw new PersonException("First name is not valid");
        if (doesNotConformRegex(lastName, "[a-zA-ZáčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ ]++"))
            throw new PersonException("Last name is not valid");
    }

    /**
     * Checks that the provided contact details are valid.
     *
     * @param email       The email of the user.
     * @param phoneNumber The phone number of the user.
     * @throws PersonException If the email or phone number is invalid, or if an account with the same email or phone number already exists.
     */
    private void checkThatContactDetailsAreValid(String email, String phoneNumber) throws PersonException{
        if (doesNotConformRegex(email, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new PersonException("Email is not valid.");
        if (personRepository.existsByEmail(email))
            throw new PersonException("Account with that email already exists.");
        if (doesNotConformRegex(phoneNumber, "^\\+?\\d[\\d -]{1,50}\\d$"))
            throw new PersonException("Phone number is not valid.");
        if (personRepository.existsByPhoneNumber(phoneNumber))
            throw new PersonException("Account with that phone number already exists.");
    }

}

