package ua.com.tracktor.kombine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile("production")
@PropertySource("classpath:application-prod.properties")
@PropertySource("classpath:credentials-prod.properties")
@EnableScheduling
public class KombineConfigProduction {
}
