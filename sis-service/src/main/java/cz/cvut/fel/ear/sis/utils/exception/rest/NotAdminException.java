package cz.cvut.fel.ear.sis.utils.exception.rest;

public class NotAdminException  extends Exception {

    public NotAdminException(String errorMessage){
        super(errorMessage);
    }
}
