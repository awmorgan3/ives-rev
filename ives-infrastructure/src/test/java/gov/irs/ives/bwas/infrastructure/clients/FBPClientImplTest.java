package gov.irs.ives.bwas.infrastructure.clients;

import gov.irs.ives.bwas.core.domain.FBPDocument;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestConfig.class)
class FBPClientImplTest {

    private final MockWebServer mockWebServer;
    private final FBPClientImpl fbpClient;

    @Value("${fbp.service.url}")
    private String fbpServiceUrl;

    public FBPClientImplTest(MockWebServer mockWebServer, FBPClientImpl fbpClient) {
        this.mockWebServer = mockWebServer;
        this.fbpClient = fbpClient;
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
    void getDocument_ShouldReturnDocument() throws ExecutionException, InterruptedException {
        // Prepare test data
        String transactionId = "test-transaction";
        String mockResponse = """
                {
                    "transactionId": "test-transaction",
                    "tin": "123456789",
                    "documentType": "F1040",
                    "documentStatus": "PENDING",
                    "authorizationStatus": "PENDING",
                    "signatureId": null,
                    "createdDate": "2024-03-15T10:30:45.123Z",
                    "updatedDate": "2024-03-15T10:30:45.123Z",
                    "documentContent": "test-content",
                    "metadata": "test-metadata"
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse));

        // Execute test
        CompletableFuture<FBPDocument> future = fbpClient.getDocument(transactionId);
        FBPDocument result = future.get();

        // Verify results
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        assertEquals("123456789", result.getTin());
        assertEquals("F1040", result.getDocumentType());
        assertEquals("PENDING", result.getDocumentStatus());
        assertEquals("PENDING", result.getAuthorizationStatus());
        assertNull(result.getSignatureId());
        assertNotNull(result.getCreatedDate());
        assertNotNull(result.getUpdatedDate());
        assertEquals("test-content", result.getDocumentContent());
        assertEquals("test-metadata", result.getMetadata());

        // Verify request
        var request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/documents/" + transactionId, request.getPath());
    }

    @Test
    void getDocuments_ShouldReturnDocumentList() throws ExecutionException, InterruptedException {
        // Prepare test data
        String tin = "123456789";
        String mockResponse = """
                {
                    "documents": [
                        {
                            "transactionId": "test-transaction-1",
                            "tin": "123456789",
                            "documentType": "F1040",
                            "documentStatus": "PENDING",
                            "authorizationStatus": "PENDING",
                            "signatureId": null,
                            "createdDate": "2024-03-15T10:30:45.123Z",
                            "updatedDate": "2024-03-15T10:30:45.123Z",
                            "documentContent": "test-content-1",
                            "metadata": "test-metadata-1"
                        },
                        {
                            "transactionId": "test-transaction-2",
                            "tin": "123456789",
                            "documentType": "W2",
                            "documentStatus": "COMPLETED",
                            "authorizationStatus": "APPROVED",
                            "signatureId": "test-signature",
                            "createdDate": "2024-03-15T10:30:45.123Z",
                            "updatedDate": "2024-03-15T10:30:45.123Z",
                            "documentContent": "test-content-2",
                            "metadata": "test-metadata-2"
                        }
                    ]
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse));

        // Execute test
        CompletableFuture<List<FBPDocument>> future = fbpClient.getDocuments(tin);
        List<FBPDocument> results = future.get();

        // Verify results
        assertNotNull(results);
        assertEquals(2, results.size());

        FBPDocument doc1 = results.get(0);
        assertEquals("test-transaction-1", doc1.getTransactionId());
        assertEquals("F1040", doc1.getDocumentType());
        assertEquals("PENDING", doc1.getDocumentStatus());

        FBPDocument doc2 = results.get(1);
        assertEquals("test-transaction-2", doc2.getTransactionId());
        assertEquals("W2", doc2.getDocumentType());
        assertEquals("COMPLETED", doc2.getDocumentStatus());
        assertEquals("APPROVED", doc2.getAuthorizationStatus());
        assertEquals("test-signature", doc2.getSignatureId());

        // Verify request
        var request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/documents?tin=" + tin, request.getPath());
    }

    @Test
    void authorize_ShouldReturnUpdatedDocument() throws ExecutionException, InterruptedException {
        // Prepare test data
        String action = "APPROVE";
        String transactionId = "test-transaction";
        String tin = "123456789";
        String signatureId = "test-signature";

        String mockResponse = """
                {
                    "transactionId": "test-transaction",
                    "tin": "123456789",
                    "documentType": "F1040",
                    "documentStatus": "COMPLETED",
                    "authorizationStatus": "APPROVED",
                    "signatureId": "test-signature",
                    "createdDate": "2024-03-15T10:30:45.123Z",
                    "updatedDate": "2024-03-15T10:30:45.123Z",
                    "documentContent": "test-content",
                    "metadata": "test-metadata"
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse));

        // Execute test
        CompletableFuture<FBPDocument> future = fbpClient.authorize(action, transactionId, tin, signatureId);
        FBPDocument result = future.get();

        // Verify results
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        assertEquals("COMPLETED", result.getDocumentStatus());
        assertEquals("APPROVED", result.getAuthorizationStatus());
        assertEquals(signatureId, result.getSignatureId());

        // Verify request
        var request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/documents/" + transactionId + "/authorize", request.getPath());
        assertTrue(request.getHeader("Content-Type").contains(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void getDocument_WhenServerError_ShouldThrowException() {
        // Prepare test data
        String transactionId = "test-transaction";

        // Prepare mock error response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        // Execute test and verify exception
        CompletableFuture<FBPDocument> future = fbpClient.getDocument(transactionId);
        assertThrows(ExecutionException.class, future::get);
    }

    @Test
    void getDocuments_WhenServerError_ShouldThrowException() {
        // Prepare test data
        String tin = "123456789";

        // Prepare mock error response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        // Execute test and verify exception
        CompletableFuture<List<FBPDocument>> future = fbpClient.getDocuments(tin);
        assertThrows(ExecutionException.class, future::get);
    }

    @Test
    void authorize_WhenServerError_ShouldThrowException() {
        // Prepare test data
        String action = "APPROVE";
        String transactionId = "test-transaction";
        String tin = "123456789";
        String signatureId = "test-signature";

        // Prepare mock error response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        // Execute test and verify exception
        CompletableFuture<FBPDocument> future = fbpClient.authorize(action, transactionId, tin, signatureId);
        assertThrows(ExecutionException.class, future::get);
    }
} 