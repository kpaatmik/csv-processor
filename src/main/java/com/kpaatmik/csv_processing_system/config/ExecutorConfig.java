package com.kpaatmik.csv_processing_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean(
            name = "recordExecutor",
            destroyMethod = "shutdown"
    )
    public ExecutorService recordExecutor() {

        return Executors.newFixedThreadPool(10);
    }

    @Bean(
            name = "jobExecutor",
            destroyMethod = "shutdown"
    )
    public Executor jobExecutor() {

        return Executors.newFixedThreadPool(2);
    }
}