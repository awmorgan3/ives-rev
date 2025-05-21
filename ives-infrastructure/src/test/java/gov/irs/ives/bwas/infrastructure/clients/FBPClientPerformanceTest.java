package gov.irs.ives.bwas.infrastructure.clients;

import gov.irs.ives.bwas.core.domain.FBPDocument;
import gov.irs.ives.bwas.infrastructure.config.TestConfig;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@SpringBootTest
@Import(TestConfig.class)
public class FBPClientPerformanceTest {

    private MockWebServer mockWebServer;
    private FBPClientImpl fbpClient;
    private String mockDocumentResponse;
    private String mockDocumentsResponse;
    private String mockAuthorizeResponse;

    @Setup
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        // Configure WebClient with mock server
        fbpClient = new FBPClientImpl(WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build());

        // Prepare mock responses
        mockDocumentResponse = """
                {
                    "transactionId": "test-transaction",
                    "tin": "123456789",
                    "documentType": "F1040",
                    "documentStatus": "PENDING",
                    "authorizationStatus": "PENDING",
                    "signatureId": null,
                    "createdDate": "2024-03-20T10:00:00Z",
                    "updatedDate": "2024-03-20T10:00:00Z",
                    "documentContent": "test-content",
                    "metadata": {}
                }
                """;

        mockDocumentsResponse = """
                [
                    {
                        "transactionId": "test-transaction-1",
                        "tin": "123456789",
                        "documentType": "F1040",
                        "documentStatus": "PENDING",
                        "authorizationStatus": "PENDING",
                        "signatureId": null,
                        "createdDate": "2024-03-20T10:00:00Z",
                        "updatedDate": "2024-03-20T10:00:00Z",
                        "documentContent": "test-content-1",
                        "metadata": {}
                    },
                    {
                        "transactionId": "test-transaction-2",
                        "tin": "123456789",
                        "documentType": "F1040",
                        "documentStatus": "PENDING",
                        "authorizationStatus": "PENDING",
                        "signatureId": null,
                        "createdDate": "2024-03-20T10:00:00Z",
                        "updatedDate": "2024-03-20T10:00:00Z",
                        "documentContent": "test-content-2",
                        "metadata": {}
                    }
                ]
                """;

        mockAuthorizeResponse = """
                {
                    "transactionId": "test-transaction",
                    "tin": "123456789",
                    "documentType": "F1040",
                    "documentStatus": "AUTHORIZED",
                    "authorizationStatus": "AUTHORIZED",
                    "signatureId": "test-signature-id",
                    "createdDate": "2024-03-20T10:00:00Z",
                    "updatedDate": "2024-03-20T10:00:00Z",
                    "documentContent": "test-content",
                    "metadata": {}
                }
                """;
    }

    @TearDown
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void getDocument_SingleRequest() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockDocumentResponse));

        CompletableFuture<FBPDocument> future = fbpClient.getDocument("test-transaction");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void getDocuments_SingleRequest() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockDocumentsResponse));

        CompletableFuture<List<FBPDocument>> future = fbpClient.getDocuments("123456789");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void authorize_SingleRequest() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockAuthorizeResponse));

        CompletableFuture<FBPDocument> future = fbpClient.authorize(
                "AUTHORIZE", "test-transaction", "123456789", "test-signature-id");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(10)
    public void getDocument_ConcurrentRequests() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockDocumentResponse));

        CompletableFuture<FBPDocument> future = fbpClient.getDocument("test-transaction");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(10)
    public void getDocuments_ConcurrentRequests() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockDocumentsResponse));

        CompletableFuture<List<FBPDocument>> future = fbpClient.getDocuments("123456789");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(10)
    public void authorize_ConcurrentRequests() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockAuthorizeResponse));

        CompletableFuture<FBPDocument> future = fbpClient.authorize(
                "AUTHORIZE", "test-transaction", "123456789", "test-signature-id");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(50)
    public void getDocument_HighLoad() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockDocumentResponse));

        CompletableFuture<FBPDocument> future = fbpClient.getDocument("test-transaction");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(50)
    public void getDocuments_HighLoad() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockDocumentsResponse));

        CompletableFuture<List<FBPDocument>> future = fbpClient.getDocuments("123456789");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(50)
    public void authorize_HighLoad() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockAuthorizeResponse));

        CompletableFuture<FBPDocument> future = fbpClient.authorize(
                "AUTHORIZE", "test-transaction", "123456789", "test-signature-id");
        future.get();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 1, time = 1)
    public void getDocument_MemoryUsage() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockDocumentResponse));

        CompletableFuture<FBPDocument> future = fbpClient.getDocument("test-transaction");
        future.get();

        // Force garbage collection to measure memory usage
        System.gc();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 1, time = 1)
    public void getDocuments_MemoryUsage() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockDocumentsResponse));

        CompletableFuture<List<FBPDocument>> future = fbpClient.getDocuments("123456789");
        future.get();

        // Force garbage collection to measure memory usage
        System.gc();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 1, time = 1)
    public void authorize_MemoryUsage() throws ExecutionException, InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockAuthorizeResponse));

        CompletableFuture<FBPDocument> future = fbpClient.authorize(
                "AUTHORIZE", "test-transaction", "123456789", "test-signature-id");
        future.get();

        // Force garbage collection to measure memory usage
        System.gc();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FBPClientPerformanceTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
} 