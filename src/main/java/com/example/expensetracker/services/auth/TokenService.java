package com.example.expensetracker.services.auth;

import com.example.expensetracker.models.user.Token;
import com.example.expensetracker.models.user.TokenType;
import com.example.expensetracker.repositories.TokenRepository;
import com.example.expensetracker.utils.exceptions.TokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    public String generateToken(UUID userId, TokenType tokenType) {
        LOGGER.info(MY_LOG_MARKER, "Generating new token with type {}", tokenType.toString());

        String token = UUID.randomUUID().toString();

        Token tokenDomain = Token.builder()
                .token(token)
                .tokenType(tokenType)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .confirmedAt(null)
                .userId(userId)
                .build();

        tokenRepository.save(tokenDomain);
        LOGGER.info(MY_LOG_MARKER, "Token generated successfully: {}", token);

        return token;
    }

    public UUID validateToken(String token) {
        var tokenDomain = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if (tokenDomain.getConfirmedAt() != null) {
            throw new IllegalArgumentException("Token already confirmed");
        }

        if (tokenDomain.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        tokenDomain.setConfirmedAt(LocalDateTime.now());

        return tokenDomain.getUserId();
    }
}
