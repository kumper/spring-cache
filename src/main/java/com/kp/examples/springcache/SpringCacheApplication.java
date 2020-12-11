package com.kp.examples.springcache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

        final Callable<Long> call = () -> service.randomSynchronized(1L);
        final var pool3 = Executors.newFixedThreadPool(9);

        log.info("Bad implementation (same cache name mentioned twice) begins");
        final var futures = pool3.invokeAll(Stream.generate(() -> call).limit(1000000).collect(Collectors.toList()));
        futures.stream().map(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).forEach(System.out::println);

        pool3.shutdown();
        pool3.awaitTermination(1, TimeUnit.MINUTES);

    }
}
