package com.superlawva.global.mail;

public interface MailService {
    void sendEmail(String to, String subject, String text);
}
