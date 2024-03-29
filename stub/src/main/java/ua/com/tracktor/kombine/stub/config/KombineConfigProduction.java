package ua.com.tracktor.kombine.stub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("production")
@PropertySource("classpath:application-prod.properties")
@PropertySource("classpath:credentials-prod.properties")
public class KombineConfigProduction {
}
