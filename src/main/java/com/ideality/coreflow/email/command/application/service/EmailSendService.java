package com.ideality.coreflow.email.command.application.service;

import com.ideality.coreflow.email.command.application.dto.UserLoginInfo;

public interface EmailSendService {

    public void sendEmailUserLoginInfo(UserLoginInfo userLoginInfo);
}
