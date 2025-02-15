package com.careers.services;

import com.careers.exceptions.EmailException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public Mono<Void> sendVerificationEmail(String to, String code) {
        return Mono.fromCallable(() -> {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message);
                    helper.setFrom("noreply@socialmedia.com");
                    helper.setTo(to);
                    helper.setSubject("Verification Code");
                    helper.setText("Your verification code is: " + code);
                    return message;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(message -> Mono.fromRunnable(() -> mailSender.send(message)))
                .onErrorResume(e -> {
                    // Add proper error handling
                    return Mono.error(new EmailException("Failed to send email", e));
                }).then();
    }
}