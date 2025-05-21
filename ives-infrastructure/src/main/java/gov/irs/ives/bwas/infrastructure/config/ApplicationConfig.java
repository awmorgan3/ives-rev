package gov.irs.ives.bwas.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application configuration class.
 */
@Configuration
@EnableAsync
@EnableTransactionManagement
@EnableConfigurationProperties
public class ApplicationConfig {
    // Configuration properties and beans will be added here as needed
} 