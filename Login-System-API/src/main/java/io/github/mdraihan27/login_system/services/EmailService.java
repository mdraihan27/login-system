package io.github.mdraihan27.login_system.services;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    private String emailBodyHtml(String verificationCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap');" +
                "body { font-family: 'Inter', sans-serif; background-color: #f4f6fb; margin: 0; padding: 30px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 8px 20px rgba(0, 0, 0, 0.07); overflow: hidden; }" +
                ".header { background: linear-gradient(135deg, #4f46e5, #3b82f6); color: white; padding: 30px 20px; text-align: center; }" +
                ".header h2 { margin: 0; font-size: 24px; }" +
                ".content { padding: 30px 40px; }" +
                ".content p { font-size: 16px; line-height: 1.7; color: #333; margin: 0 0 20px; }" +
                ".code-box { margin: 30px 0; padding: 18px; text-align: center; background-color: #eef4ff; border-left: 5px solid #3b82f6; font-size: 30px; font-weight: 600; letter-spacing: 4px; color: #1e3a8a; border-radius: 8px; animation: pop 0.4s ease-in-out; }" +
                "@keyframes pop { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }" +
                ".footer { font-size: 13px; text-align: center; color: #999; padding: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>Email Verification</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hello,</p>" +
                "<p>Use the code below to verify your email address:</p>" +
                "<div class='code-box'>" + verificationCode + "</div>" +
                "<p>This code will expire shortly. If you did not request this, feel free to ignore the email.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "&copy; github.com/mdraihan27 — All rights reserved." +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    @Transactional
    public ResponseEntity<?> sendEmail(String to, String subject, String verificationCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailBodyHtml(verificationCode), true); // 'true' enables HTML

            javaMailSender.send(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Exception while sending email", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
