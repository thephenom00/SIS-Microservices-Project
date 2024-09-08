package cz.cvut.fel.ear.sis.utils.exception.rest;

public class NotFoundException extends Exception {

    public NotFoundException(String errorMessage){
        super(errorMessage);
    }
}
