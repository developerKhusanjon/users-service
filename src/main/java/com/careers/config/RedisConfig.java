package com.careers.config;

import com.careers.model.SessionDocument;
import com.careers.model.UserDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory userDocsConnectionFactory() {
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(redisProperties.getUserDocs().getHost(),
                        redisProperties.getUserDocs().getPort()));
    }

    @Bean
    public ReactiveRedisConnectionFactory sessionsConnectionFactory() {
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(redisProperties.getSessions().getHost(),
                        redisProperties.getSessions().getPort()));
    }

    @Bean
    public ReactiveRedisTemplate<String, UserDocument> userDocsTemplate() {
        RedisSerializationContext<String, UserDocument> context =
                RedisSerializationContext.<String, UserDocument>newSerializationContext()
                        .key(StringRedisSerializer.UTF_8)
                        .value(new Jackson2JsonRedisSerializer<>(UserDocument.class))
                        .build();
        return new ReactiveRedisTemplate<>(userDocsConnectionFactory(), context);
    }


    @Bean
    public ReactiveRedisTemplate<String, SessionDocument> sessionsTemplate() {
        RedisSerializationContext<String, SessionDocument> context =
                RedisSerializationContext.<String, SessionDocument>newSerializationContext()
                        .key(StringRedisSerializer.UTF_8)
                        .value(new Jackson2JsonRedisSerializer<>(SessionDocument.class))
                        .build();
        return new ReactiveRedisTemplate<>(sessionsConnectionFactory(), context);
    }

    @Bean
    public ReactiveRedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("redis");
        config.setPort(6379);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> redisTemplate() {
        RedisSerializationContext<String, Object> context =
                RedisSerializationContext.newSerializationContext()
                        .key(StringRedisSerializer.UTF_8)
                        .value(new Jackson2JsonRedisSerializer<>(Object.class))
                        .hashKey(StringRedisSerializer.UTF_8)
                        .hashValue(new Jackson2JsonRedisSerializer<>(Object.class))
                        .build();

        return new ReactiveRedisTemplate<>(redisConnectionFactory(), context);
    }

    @Bean
    public ReactiveRedisSearchOperations redisSearchOperations() {
        return new ReactiveRedisSearchTemplate(redisTemplate());
    }

    @Bean
    public RedisSerializationContext<String, Object> redisSerializationContext() {
        CryptoRedisSerializer cryptoSerializer = new CryptoRedisSerializer(encryptionKey);

        return RedisSerializationContext.<String, Object>newSerializationContext()
                .key(StringRedisSerializer.UTF_8)
                .value(cryptoSerializer)
                .hashKey(StringRedisSerializer.UTF_8)
                .hashValue(cryptoSerializer)
                .build();
    }
}