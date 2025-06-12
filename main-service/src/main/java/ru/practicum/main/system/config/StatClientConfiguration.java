package ru.practicum.main.system.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "ru.practicum.client" })
public class StatClientConfiguration {
}