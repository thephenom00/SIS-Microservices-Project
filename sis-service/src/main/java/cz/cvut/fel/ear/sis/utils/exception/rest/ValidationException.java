package cz.cvut.fel.ear.sis.utils.exception.rest;

public class ValidationException extends Exception {

    public ValidationException(String errorMessage){
        super(errorMessage);
    }
}
