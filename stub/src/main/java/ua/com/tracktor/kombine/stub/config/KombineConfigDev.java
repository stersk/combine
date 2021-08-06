package ua.com.tracktor.kombine.stub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("dev")
@PropertySource("classpath:application-dev.properties")
@PropertySource("classpath:credentials-dev.properties")
public class KombineConfigDev {
}
