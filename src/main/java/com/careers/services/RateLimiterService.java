package com.careers.services;

import com.careers.config.TwilioProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class RateLimiterService {
    private final Cache<String, RequestCounter> requestCache;

    public RateLimiterService(TwilioProperties properties) {
        this.requestCache = Caffeine.newBuilder()
            .expireAfterWrite(properties.getRateLimit().getWindowDuration())
            .maximumSize(100_000)
            .build();
    }

    public Mono<Boolean> allowRequest(String clientIdentifier) {
        return Mono.fromCallable(() -> 
            requestCache.asMap().compute(clientIdentifier, (key, counter) -> {
                if (counter == null) return new RequestCounter();
                if (counter.getCount() >= maxAttempts) return counter;
                counter.increment();
                return counter;
            }))
            .map(counter -> counter.getCount() <= maxAttempts);
    }

    @Data
    private static class RequestCounter {
        private int count = 1;
        private Instant created = Instant.now();

        public void increment() {
            count++;
        }
    }
}