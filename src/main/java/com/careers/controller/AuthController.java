package com.careers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public Mono<AuthResponse> refreshTokens(@RequestBody RefreshRequest request) {
        return userRepository.findByRefreshToken(request.refreshToken())
            .flatMap(user -> {
                jwtService.invalidateTokens(user.getId());
                return jwtService.generateTokens(user);
            })
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED)));
    }

    @PostMapping("/logout")
    public Mono<Void> logout(@AuthenticationPrincipal SecurityProperties.User user) {
        return jwtService.invalidateTokens(user.getId());
    }
}