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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;


    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(true);

        userRepository.save(user);
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

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
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

    // for testing

    public boolean checkEncodedPassword(LoginRequest loginRequest) throws AuthenticationFailedException {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        String password = user.orElseThrow(() -> new EduToolException("Invalid username")).getPassword();
        if(password.equals(loginRequest.getPassword())){
            throw new AuthenticationFailedException("PASSWORD_IS_NOT_ENCODED");
        }
        return true;
    }

    public boolean checkIfVerificationTokenExists(LoginRequest loginRequest) throws AuthenticationFailedException {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUser(user.orElseThrow(() -> new EduToolException("Invalid username")));
        if(verificationToken == null){
            throw new AuthenticationFailedException("THERE_IS_NO_VERIFICATION_TOKEN");
        }
        return true;
    }
}
