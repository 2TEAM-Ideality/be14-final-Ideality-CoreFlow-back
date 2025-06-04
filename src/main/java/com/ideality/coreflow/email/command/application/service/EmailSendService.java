package com.ideality.coreflow.email.command.application.service;

import com.ideality.coreflow.email.command.domail.aggregate.EmailType;

import java.util.Map;

public interface EmailSendService {

    void sendEmail(EmailType emailType, Map<String, String> data);
}
