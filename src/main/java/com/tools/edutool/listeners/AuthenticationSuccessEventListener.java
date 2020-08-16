package com.tools.edutool.listeners;

import com.tools.edutool.model.User;
import com.tools.edutool.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    public void onApplicationEvent(AuthenticationSuccessEvent e) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)
                e.getAuthentication().getPrincipal();

        loginAttemptService.loginSucceeded(user.getUsername());
    }
}