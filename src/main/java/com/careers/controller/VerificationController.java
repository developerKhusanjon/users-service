package com.careers.controller;

import com.careers.model.VerificationRequest;
import com.careers.services.TwilioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class VerificationController {
    private final TwilioService twilioService;

    @PostMapping("/send-verification-code")
    public Mono<Void> sendVerificationCode(
        @RequestBody VerificationRequest request,
        @RequestHeader("X-Device-Id") String deviceId
    ) {
        return twilioService.sendVerificationCode(request.getTo(), deviceId);
    }
}