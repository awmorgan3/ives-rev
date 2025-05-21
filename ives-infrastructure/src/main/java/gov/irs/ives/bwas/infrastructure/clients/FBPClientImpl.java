package gov.irs.ives.bwas.infrastructure.clients;

import gov.irs.ives.bwas.core.domain.FBPDocument;
import gov.irs.ives.bwas.core.ports.FBPClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class FBPClientImpl implements FBPClient {

    private final WebClient webClient;

    @Value("${fbp.service.url}")
    private String fbpServiceUrl;

    @Override
    public CompletableFuture<FBPDocument> getDocument(String transactionId) {
        return webClient.get()
                .uri(fbpServiceUrl + "/documents/" + transactionId)
                .retrieve()
                .bodyToMono(FBPDocumentResponse.class)
                .map(this::mapToFBPDocument)
                .toFuture();
    }

    @Override
    public CompletableFuture<List<FBPDocument>> getDocuments(String tin) {
        return webClient.get()
                .uri(fbpServiceUrl + "/documents?tin=" + tin)
                .retrieve()
                .bodyToMono(FBPDocumentListResponse.class)
                .map(response -> response.documents().stream()
                        .map(this::mapToFBPDocument)
                        .toList())
                .toFuture();
    }

    @Override
    public CompletableFuture<FBPDocument> authorize(String action, String transactionId, String tin, String signatureId) {
        return webClient.post()
                .uri(fbpServiceUrl + "/documents/" + transactionId + "/authorize")
                .bodyValue(new FBPAuthorizationRequest(action, tin, signatureId))
                .retrieve()
                .bodyToMono(FBPDocumentResponse.class)
                .map(this::mapToFBPDocument)
                .toFuture();
    }

    private FBPDocument mapToFBPDocument(FBPDocumentResponse response) {
        return FBPDocument.builder()
                .transactionId(response.transactionId())
                .tin(response.tin())
                .documentType(response.documentType())
                .documentStatus(response.documentStatus())
                .authorizationStatus(response.authorizationStatus())
                .signatureId(response.signatureId())
                .createdDate(response.createdDate())
                .updatedDate(response.updatedDate())
                .documentContent(response.documentContent())
                .metadata(response.metadata())
                .build();
    }

    private record FBPAuthorizationRequest(
            String action,
            String tin,
            String signatureId) {}

    private record FBPDocumentResponse(
            String transactionId,
            String tin,
            String documentType,
            String documentStatus,
            String authorizationStatus,
            String signatureId,
            LocalDateTime createdDate,
            LocalDateTime updatedDate,
            String documentContent,
            String metadata) {}

    private record FBPDocumentListResponse(
            List<FBPDocumentResponse> documents) {}
} 