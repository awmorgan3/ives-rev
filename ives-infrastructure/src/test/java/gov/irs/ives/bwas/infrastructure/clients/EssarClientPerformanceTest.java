package gov.irs.ives.bwas.infrastructure.clients;

import gov.irs.ives.bwas.core.domain.EssarSignature;
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
public class EssarClientPerformanceTest {

    private MockWebServer mockWebServer;
    private EssarClientImpl essarClient;
    private String mockResponse;

    @Setup
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        // Configure WebClient with mock server
        essarClient = new EssarClientImpl(WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build());

        // Prepare mock response
        mockResponse = """
                {
                    "signatureId": "test-signature-id",
                    "status": "SUCCESS",
                    "signatureValue": "test-signature-value"
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
    public void getElectronicSignature_SingleRequest() throws ExecutionException, InterruptedException {
        // Prepare mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse));

        // Execute test
        CompletableFuture<EssarSignature> future = essarClient.getElectronicSignature(
                "test-uuid", "test-transaction", "test-user", "123456789",
                "F1040", "BWAS", "test-intent");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(10)
    public void getElectronicSignature_ConcurrentRequests() throws ExecutionException, InterruptedException {
        // Prepare mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse));

        // Execute test
        CompletableFuture<EssarSignature> future = essarClient.getElectronicSignature(
                "test-uuid", "test-transaction", "test-user", "123456789",
                "F1040", "BWAS", "test-intent");
        future.get();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(50)
    public void getElectronicSignature_HighLoad() throws ExecutionException, InterruptedException {
        // Prepare mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse));

        // Execute test
        CompletableFuture<EssarSignature> future = essarClient.getElectronicSignature(
                "test-uuid", "test-transaction", "test-user", "123456789",
                "F1040", "BWAS", "test-intent");
        future.get();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 1, time = 1)
    public void getElectronicSignature_MemoryUsage() throws ExecutionException, InterruptedException {
        // Prepare mock response
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(mockResponse));

        // Execute test
        CompletableFuture<EssarSignature> future = essarClient.getElectronicSignature(
                "test-uuid", "test-transaction", "test-user", "123456789",
                "F1040", "BWAS", "test-intent");
        future.get();

        // Force garbage collection to measure memory usage
        System.gc();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(EssarClientPerformanceTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
} 