package com.superlawva.global.mail;

public interface MailSender {
    void send(String to, String subject, String html);
}
