package ua.com.tracktor.combine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@PropertySource("classpath:credentials.properties")
public class CombineConfig {
}
