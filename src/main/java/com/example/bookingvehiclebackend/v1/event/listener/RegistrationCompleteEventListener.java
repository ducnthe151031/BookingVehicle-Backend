package com.example.bookingvehiclebackend.v1.event.listener;

import com.example.bookingvehiclebackend.v1.dto.User;
import com.example.bookingvehiclebackend.v1.event.PasswordResetEvent;
import com.example.bookingvehiclebackend.v1.event.RegistrationCompleteEvent;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.service.AuthenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener  implements ApplicationListener<ApplicationEvent> {
    private final AuthenService authenService;
    private final JavaMailSender mailSender;
    User user;
    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        if (event instanceof RegistrationCompleteEvent registrationCompleteEvent) {
            handleRegistrationCompleteEvent(registrationCompleteEvent);
        } else if (event instanceof PasswordResetEvent passwordResetEvent) {
            handlePasswordResetEvent(passwordResetEvent);
        }
    }

    private void handleRegistrationCompleteEvent(RegistrationCompleteEvent event) {
        user = event.getUser();
        String jwtToken = event.getJwtToken();
        authenService.saveUserToken(user, jwtToken);
        String url = event.getApplicationUrl() + "/v1/user/verify-email?token=" + jwtToken;
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.SEND_MAIL_ERROR);
        }
        log.info("Click the link to verify your registration :  {}", url);
    }
    // Handle password reset event
    private void handlePasswordResetEvent(PasswordResetEvent event) {
        user = event.getUser();
        String resetToken = event.getResetToken();
        authenService.saveUserToken(user, resetToken);
        String url = event.getApplicationUrl()  + resetToken;
        try {
            sendResetPasswordEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.SEND_MAIL_ERROR);
        }
        log.info("Click the link to reset your password: {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>Thank you for registering with us, " +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                "<p> Thank you <br> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);

        messageHelper.setFrom("hieunmhe170629@fpt.edu.vn", senderName); //set = mail cua minh

        messageHelper.setFrom("ducnthe151031@fpt.edu.vn", senderName); //set = mail cua minh

        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
    // Send reset password email
    private void sendResetPasswordEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + user.getUsername() + ", </p>" +
                "<p>We received a request to reset your password. " +
                "Please follow the link below to reset your password:</p>" +
                "<a href=\"" + url + "\">Reset your password</a>" +
                "<p>If you did not request this, please ignore this email.<br>Thank you,<br>User Registration Portal Service</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);

        messageHelper.setFrom("hieunmhe170629@fpt.edu.vn", senderName);

        messageHelper.setFrom("ducnthe151031@fpt.edu.vn", senderName);

        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
