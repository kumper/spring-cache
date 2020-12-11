package com.kp.examples.springcache;

import org.springframework.cache.annotation.Cacheable;

import static java.lang.String.format;

@org.springframework.stereotype.Service
public class Service {

    @Cacheable(value = "randomSynchronized", cacheManager = CacheConfig.SHORT_LIVE, sync = true)
    public Long randomSynchronized(Long param) {
        return random(param);
    }

    @Cacheable(value = "randomUnsynchronized", cacheManager = CacheConfig.SHORT_LIVE)
    public Long randomUnsynchronized(Long param) {
        return random(param);
    }

    private Long random(Long param) {
        final var result = (long) (Math.random() * 100);
        System.out.println(format("[%s] Value for %d is %d",
                Thread.currentThread().getName(),
                param,
                result
        ));

        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
