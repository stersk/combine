package ua.com.tracktor.kombine.config;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.*;

@Configuration
@Profile("dev-mobile")
@PropertySource("classpath:application-dev-mobile.properties")
@PropertySource("classpath:credentials-dev-mobile.properties")
@EnableScheduling
public class KombineConfigDevMobile implements WebMvcConfigurer {
    @Value("${server.port}")
    private String serverPort;

    @Value("${management.port:${server.port}}")
    private String managementPort;

    @Value("${server.additionalPorts:null}")
    private String additionalPorts;

    @Value("${server.ssl.key-store}")
    private String sslKeyStore;

    @Value("${server.ssl.key-store-password}")
    private String sslKeyStorePassword;

    @Value("${server.ssl.key-alias}")
    private String sslKeyAlias;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {

        return factory -> {
            Connector[] additionalConnectors = additionalConnector();
            if (additionalConnectors.length > 0) {
                factory.addAdditionalTomcatConnectors(additionalConnectors);
            }
        };
    }

    private Connector[] additionalConnector() {
        if (this.additionalPorts == null || this.additionalPorts.equals("")) {
            return new Connector[0];
        }
        Set<String> defaultPorts = new HashSet<>(Arrays.asList(this.serverPort, this.managementPort));
        String[] ports = this.additionalPorts.split(",");
        List<Connector> result = new ArrayList<>();
        for (String port : ports) {
            if (StringUtils.hasText(port) && !"null".equalsIgnoreCase(port) && !defaultPorts.contains(port)) {
                Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                connector.setScheme("https");
                connector.setSecure(true);
                connector.setProperty("SSLEnabled", "true");
                connector.setPort(Integer.parseInt(port.trim()));

                SSLHostConfig sslConfig = new SSLHostConfig();

                SSLHostConfigCertificate certConfig = new SSLHostConfigCertificate(sslConfig, SSLHostConfigCertificate.Type.RSA);
                certConfig.setCertificateKeystoreFile(sslKeyStore);
                certConfig.setCertificateKeystorePassword(sslKeyStorePassword);
                certConfig.setCertificateKeyAlias(sslKeyAlias);
                sslConfig.addCertificate(certConfig);

                connector.addSslHostConfig(sslConfig);

                result.add(connector);
            }
        }
        return result.toArray(new Connector[] {});
    }

    @Bean
    public HttpTraceRepository htttpTraceRepository()
    {
        return new InMemoryHttpTraceRepository();
    }
}