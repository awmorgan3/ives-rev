package gov.irs.ives.bwas.infrastructure.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.irs.ives.bwas.core.domain.EssarSignature;
import gov.irs.ives.bwas.infrastructure.config.TestConfig;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestConfig.class)
class EssarClientImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockWebServer mockWebServer;
    private final EssarClientImpl essarClient;

    @Value("${essar.service.url}")
    private String essarServiceUrl;

    public EssarClientImplTest(MockWebServer mockWebServer, EssarClientImpl essarClient) {
        this.mockWebServer = mockWebServer;
        this.essarClient = essarClient;
    }

    @BeforeEach
    void setUp() {
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getElectronicSignature_ShouldReturnSignature() throws ExecutionException, InterruptedException {
        // Prepare test data
        String uuid = "test-uuid";
        String transactionId = "test-transaction";
        String userName = "test-user";
        String tin = "123456789";
        String formType = "F1040";
        String appName = "BWAS";
        String intentId = "test-intent";

        // Prepare mock response
        String mockResponse = """
                {
                    "signatureId": "test-signature-id",
                    "status": "SUCCESS",
                    "signatureValue": "test-signature-value"
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse));

        // Execute test
        CompletableFuture<EssarSignature> future = essarClient.getElectronicSignature(
                uuid, transactionId, userName, tin, formType, appName, intentId);
        EssarSignature result = future.get();

        // Verify results
        assertNotNull(result);
        assertEquals("test-signature-id", result.getSignatureId());
        assertEquals("SUCCESS", result.getSignatureStatus());
        assertEquals("test-signature-value", result.getSignatureValue());
        assertEquals(uuid, result.getUuid());
        assertEquals(transactionId, result.getTransactionId());
        assertEquals(userName, result.getUserName());
        assertEquals(tin, result.getTin());
        assertEquals(formType, result.getFormType());
        assertEquals(appName, result.getAppName());
        assertEquals(intentId, result.getIntentId());
        assertNotNull(result.getSignatureDate());

        // Verify request
        var request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/signatures", request.getPath());
        assertTrue(request.getHeader("Content-Type").contains(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getElectronicSignature_WhenServerError_ShouldThrowException() {
        // Prepare test data
        String uuid = "test-uuid";
        String transactionId = "test-transaction";
        String userName = "test-user";
        String tin = "123456789";
        String formType = "F1040";
        String appName = "BWAS";
        String intentId = "test-intent";

        // Prepare mock error response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        // Execute test and verify exception
        CompletableFuture<EssarSignature> future = essarClient.getElectronicSignature(
                uuid, transactionId, userName, tin, formType, appName, intentId);
        
        assertThrows(ExecutionException.class, future::get);
    }

    @Test
    void getElectronicSignature_WhenInvalidResponse_ShouldThrowException() {
        // Prepare test data
        String uuid = "test-uuid";
        String transactionId = "test-transaction";
        String userName = "test-user";
        String tin = "123456789";
        String formType = "F1040";
        String appName = "BWAS";
        String intentId = "test-intent";

        // Prepare mock invalid response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("invalid json"));

        // Execute test and verify exception
        CompletableFuture<EssarSignature> future = essarClient.getElectronicSignature(
                uuid, transactionId, userName, tin, formType, appName, intentId);
        
        assertThrows(ExecutionException.class, future::get);
    }
} 