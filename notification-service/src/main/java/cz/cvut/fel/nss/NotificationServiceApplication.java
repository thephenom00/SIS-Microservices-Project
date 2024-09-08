package cz.cvut.fel.nss;

import cz.cvut.fel.nss.event.EnrollmentGradedEvent;
import cz.cvut.fel.nss.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    private final EmailService emailService;

    @Autowired
    public NotificationServiceApplication(EmailService emailService) {
        this.emailService = emailService;
    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    /**
     * sends an email to the student when the enrollment is graded
     * @param event the event that contains the student username
     */
    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(EnrollmentGradedEvent event) {
        emailService.sendEmail(event);
        log.info("Student " + event.getStudentUsername() + " has been graded");
    }
}
