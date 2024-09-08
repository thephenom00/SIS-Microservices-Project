package cz.cvut.fel.nss.service;

import cz.cvut.fel.nss.event.EnrollmentGradedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends an email to the student with the grade of the course.
     *
     * @param event The event containing the grade and course information.
     */
    public void sendEmail(EnrollmentGradedEvent event) {
        String email = event.getStudentUsername() + "@fel.cvut.cz";
        String subject = "SIS - [SIS NOTIFICATION]";
        String body =
                "This email was sent from the SIS Study Information System.\n" +
                        "------------------------------------------------------------------------\n" +
                        "You have received the grade of " + event.getGrade() + " in the subject " + event.getCourse() + " by the teacher " + event.getTeacherFullName() + ".";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ahoj@gmail.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
        log.info("Email sent to " + email);
    }

}
