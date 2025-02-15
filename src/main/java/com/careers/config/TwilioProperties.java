package com.careers.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "twilio")
@Getter
@Setter
public class TwilioProperties {
    private String accountSid;
    private String authToken;
    private String serviceSid;
    private String fromNumber;
    private RateLimit rateLimit = new RateLimit();

    @Getter
    @Setter
    public static class RateLimit {
        private int maxAttempts = 5;
        private Duration windowDuration = Duration.ofMinutes(10);
    }
}