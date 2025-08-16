package com.example.bookingvehiclebackend.v1.event.listener;

import com.example.bookingvehiclebackend.v1.dto.RentalRequest;
import com.example.bookingvehiclebackend.v1.dto.User;
import com.example.bookingvehiclebackend.v1.event.PasswordResetEvent;
import com.example.bookingvehiclebackend.v1.event.RegistrationCompleteEvent;
import com.example.bookingvehiclebackend.v1.event.RentalRequestEvent;
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
public class EventListener  implements ApplicationListener<ApplicationEvent> {
    private final AuthenService authenService;
    private final JavaMailSender mailSender;
    User user;
    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        if (event instanceof RegistrationCompleteEvent registrationCompleteEvent) {
            handleRegistrationCompleteEvent(registrationCompleteEvent);
        } else if (event instanceof PasswordResetEvent passwordResetEvent) {
            handlePasswordResetEvent(passwordResetEvent);
        } else if(event instanceof RentalRequestEvent rentalRequestEvent){
            bookingVehicleSuccess(rentalRequestEvent) ;
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
        messageHelper.setFrom("btung548@gmail.com", senderName); //set = mail cua minh
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
        messageHelper.setFrom("btung548@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }

    private void bookingVehicleSuccess(RentalRequestEvent event) {
        try {
            user = event.getUser();
            String jwtToken = event.getJwtToken();
            authenService.saveUserToken(user, jwtToken);
            sendBookingVehicle(event);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendBookingVehicle(RentalRequestEvent event) throws MessagingException, UnsupportedEncodingException {
        RentalRequest rentalRequest = event.getRentalRequest();

        String subject = "Hợp đồng thuê xe";
        String senderName = "Dịch vụ Đặt xe";

        String mailContent = "<div style='font-family: Arial, sans-serif; line-height:1.6; color:#333;'>"
                + "<h2 style='color:#2c3e50;'>🚗 Hợp đồng thuê xe</h2>"

                + "<h3>Thông tin hợp đồng</h3>"
                + "<p>Hợp đồng này được lập giữa <b>Công ty Cho thuê xe ABC</b> (Bên cho thuê) và <b>"
                + rentalRequest.getCreatedBy() + "</b> (Bên thuê) cho việc thuê xe dưới đây:</p>"

                + "<table style='width:100%; border-collapse:collapse;'>"

                + "  <tr>"
                + "    <td><b>Mã số hợp đồng:</b> " + rentalRequest.getId() + "</td>"
                + "    <td><b>Thời gian thuê:</b> Từ " + rentalRequest.getStartDate() + " đến " + rentalRequest.getEndDate() + "</td>"
                + "    <td><b>Tổng chi phí:</b> " + rentalRequest.getTotalPrice() + " VND</td>"
                + "  </tr>"
                + "</table>"

                + "<h3>Điều khoản hợp đồng</h3>"
                + "<ul>"
                + "  <li>✔️ Bên thuê cam kết sử dụng xe đúng mục đích và tuân thủ mọi quy định giao thông.</li>"
                + "  <li>✔️ Bên thuê chịu trách nhiệm bồi thường thiệt hại nếu xe bị hư hỏng do lỗi sử dụng.</li>"
                + "  <li>✔️ Nếu bạn trả xe muộn, phí phạt sẽ được tính theo công thức: (giá thuê theo giờ) × (số giờ muộn).</li>"
                + "  <li>✔️ Bên cho thuê có quyền thu hồi xe nếu phát hiện vi phạm hợp đồng.</li>"
                + "</ul>"

                + "<h3>Cam kết của các bên</h3>"
                + "<p>Cả hai bên cam kết thực hiện đúng các điều khoản trong hợp đồng này. "
                + "Mọi tranh chấp sẽ được giải quyết thông qua thương lượng hoặc tại tòa án có thẩm quyền.</p>"


                + "<p style='margin-top:30px;'>Trân trọng,<br><b>Dịch vụ Đặt xe</b></p>"
                + "</div>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        messageHelper.setFrom("btung548@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);

        mailSender.send(message);
    }
}
