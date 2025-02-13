package com.careers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    
    @Bean
    public ReactiveRedisTemplate<String, UserAuthDetails> authDetailsTemplate(
        ReactiveRedisConnectionFactory factory
    ) {
        RedisSerializationContext<String, UserAuthDetails> context = 
            RedisSerializationContext.<String, UserAuthDetails>newSerializationContext()
                .key(StringRedisSerializer.UTF_8)
                .value(new Jackson2JsonRedisSerializer<>(UserAuthDetails.class))
                .build();
        
        return new ReactiveRedisTemplate<>(factory, context);
    }
}