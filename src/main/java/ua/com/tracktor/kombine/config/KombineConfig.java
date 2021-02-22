package ua.com.tracktor.kombine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@PropertySource("classpath:application-dev.properties")
//@PropertySource("classpath:credentials-dev.properties")
@PropertySource("classpath:application-prod.properties")
@PropertySource("classpath:credentials-prod.properties")
public class KombineConfig {
}
