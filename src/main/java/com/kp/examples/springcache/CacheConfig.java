package com.kp.examples.springcache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String SHORT_LIVE = "shortLivingCacheManager";

    @Bean
    public Caffeine shortLivingCaffeine() {
        return Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS);
    }

    @Bean
    @Qualifier(SHORT_LIVE)
    @Primary
    public CacheManager shortLivingCacheManager() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(shortLivingCaffeine());
        caffeineCacheManager.setCacheNames(List.of(
                "randomSynchronized",
                "randomUnsynchronized"
        ));
        return caffeineCacheManager;
    }
}
