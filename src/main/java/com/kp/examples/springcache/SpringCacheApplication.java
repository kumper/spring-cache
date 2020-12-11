package com.kp.examples.springcache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@SpringBootApplication
@Slf4j
public class SpringCacheApplication implements CommandLineRunner {

    @Autowired
    private Service service;

    public static void main(String[] args) {
        SpringApplication.run(SpringCacheApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final var pool1 = Executors.newFixedThreadPool(9);

        log.info("Bad implementation begins");
        for (int i = 0; i < 10; i++) {
            pool1.submit(() -> {
                service.randomUnsynchronized(1L);
                service.randomUnsynchronized(2L);
                service.randomUnsynchronized(3L);
            });
        }

        pool1.shutdown();
        pool1.awaitTermination(1, TimeUnit.MINUTES);

        final var pool2 = Executors.newFixedThreadPool(9);

        log.info("Right implementation begins");
        for (int i = 0; i < 10; i++) {
            pool2.submit(() -> {
                service.randomSynchronized(1L);
                service.randomSynchronized(2L);
                service.randomSynchronized(3L);
            });
        }

        pool2.shutdown();
        pool2.awaitTermination(1, TimeUnit.MINUTES);
    }


}
