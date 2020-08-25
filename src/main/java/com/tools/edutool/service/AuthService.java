package com.tools.edutool.service;

import com.tools.edutool.dto.AuthenticationResponse;
import com.tools.edutool.dto.LoginRequest;
import com.tools.edutool.dto.RefreshTokenRequest;
import com.tools.edutool.dto.RegisterRequest;
import com.tools.edutool.model.NotificationEmail;
import com.tools.edutool.model.User;
import com.tools.edutool.model.VerificationToken;
import com.tools.edutool.repository.UserRepository;
import com.tools.edutool.repository.VerificationTokenRepository;
import com.tools.edutool.exceptions.EduToolException;
import com.tools.edutool.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.AuthenticationFailedException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    private static final String SUCCESSFUL_LOGIN_MESSAGE = "Login successful";
    private static final String FAILED_LOGIN_MESSAGE = "Login failed";

    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setCreated(Instant.now());
        user.setEnabled(true);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Rate the App, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }
    
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EduToolException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new EduToolException("Invalid Token")));
    }

    public AuthenticationResponse login(LoginRequest loginRequest) throws AuthenticationFailedException {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        String password = user.orElseThrow(() -> new EduToolException("Invalid username")).getPassword();
        String responseMessage;
        if(password != null  && password.equals(loginRequest.getPassword())){
            responseMessage = SUCCESSFUL_LOGIN_MESSAGE;
        } else{
            throw new AuthenticationFailedException(FAILED_LOGIN_MESSAGE);
        }

        return AuthenticationResponse.builder()
                .username(loginRequest.getUsername())
                .responseMessage(responseMessage)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public boolean checkEncodedPassword(LoginRequest loginRequest) throws AuthenticationFailedException {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        String password = user.orElseThrow(() -> new EduToolException("Invalid username")).getPassword();
        if(password.equals(loginRequest.getPassword())){
            throw new AuthenticationFailedException("PASSWORD_IS_NOT_ENCODED");
        }
        return true;
    }
}
