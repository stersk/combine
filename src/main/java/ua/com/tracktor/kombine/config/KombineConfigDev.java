package ua.com.tracktor.kombine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile("dev")
@PropertySource("classpath:application-dev.properties")
@PropertySource("classpath:credentials-dev.properties")
@EnableScheduling
public class KombineConfigDev {
}
