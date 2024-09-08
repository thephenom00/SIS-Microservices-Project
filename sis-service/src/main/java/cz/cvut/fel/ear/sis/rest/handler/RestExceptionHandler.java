package cz.cvut.fel.ear.sis.rest.handler;
import cz.cvut.fel.ear.sis.utils.exception.*;
import cz.cvut.fel.ear.sis.utils.exception.rest.NotFoundException;
import cz.cvut.fel.ear.sis.utils.exception.rest.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static void logException(Exception ex) {
        LOG.error("Exception caught:", ex);
    }

    private static ErrorInfo errorInfo(HttpServletRequest request, Throwable e) {
        return new ErrorInfo(e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorInfo> persistenceException(HttpServletRequest request, PersistenceException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e.getCause()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> resourceNotFound(HttpServletRequest request, NotFoundException e) {
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorInfo> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(ClassroomException.class)
    public ResponseEntity<ErrorInfo> handleClassroomException(HttpServletRequest request, ClassroomException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CourseException.class)
    public ResponseEntity<ErrorInfo> handleCourseException(HttpServletRequest request, CourseException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EnrollmentException.class)
    public ResponseEntity<ErrorInfo> handleEnrollmentException(HttpServletRequest request, EnrollmentException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParallelException.class)
    public ResponseEntity<ErrorInfo> handleParallelException(HttpServletRequest request, ParallelException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PersonException.class)
    public ResponseEntity<ErrorInfo> handlePersonException(HttpServletRequest request, PersonException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SemesterException.class)
    public ResponseEntity<ErrorInfo> handleSemesterException(HttpServletRequest request, SemesterException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentException.class)
    public ResponseEntity<ErrorInfo> handleStudentException(HttpServletRequest request, StudentException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.BAD_REQUEST);
    }


}


