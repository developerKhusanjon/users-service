package com.careers.services;

import com.careers.config.TwilioProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.netty.util.internal.ThreadLocalRandom;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class TwilioService {
    private final TwilioProperties properties;
    private final RateLimiterService rateLimiter;
    private final Cache<String, String> verificationCache;

    @PostConstruct
    public void init() {
        Twilio.init(properties.getAccountSid(), properties.getAuthToken());
    }

    public Mono<Void> sendVerificationCode(String to, String clientIdentifier) {
        return rateLimiter.allowRequest(clientIdentifier)
            .flatMap(allowed -> {
                if (!allowed) {
                    return Mono.error(new RateLimitExceededException());
                }
                return getOrCreateVerificationCode(to)
                    .flatMap(code -> sendSms(to, code));
            });
    }

    private Mono<String> getOrCreateVerificationCode(String to) {
        return Mono.fromCallable(() -> verificationCache.get(to, key -> {
            String code = generateRandomCode();
            verificationCache.put(to, code);
            return code;
        }));
    }

    private Mono<Void> sendSms(String to, String code) {
        return Mono.fromCallable(() -> {
            if (to.contains("@")) {
                // For email (using Twilio SendGrid if configured)
                sendEmail(to, code);
            } else {
                // For SMS
                Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(properties.getFromNumber()),
                    "Your verification code: " + code
                ).create();
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private String generateRandomCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(999999));
    }
}