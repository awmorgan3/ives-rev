package gov.irs.ives.bwas.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import okhttp3.mockwebserver.MockWebServer;

@TestConfiguration
public class TestConfig {

    @Bean
    public MockWebServer mockWebServer() {
        return new MockWebServer();
    }

    @Bean
    public WebClient webClient(MockWebServer mockWebServer) {
        return WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
    }
} 