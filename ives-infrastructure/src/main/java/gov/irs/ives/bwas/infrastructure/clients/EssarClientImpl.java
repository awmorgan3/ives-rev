package gov.irs.ives.bwas.infrastructure.clients;

import gov.irs.ives.bwas.core.domain.EssarSignature;
import gov.irs.ives.bwas.core.ports.EssarClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class EssarClientImpl implements EssarClient {

    private final WebClient webClient;

    @Value("${essar.service.url}")
    private String essarServiceUrl;

    @Override
    public CompletableFuture<EssarSignature> getElectronicSignature(
            String uuid, String transactionId, String userName, String tin,
            String formType, String appName, String intentId) {
        
        return webClient.post()
                .uri(essarServiceUrl + "/signatures")
                .bodyValue(new EssarSignatureRequest(uuid, transactionId, userName, tin, formType, appName, intentId))
                .retrieve()
                .bodyToMono(EssarSignatureResponse.class)
                .map(response -> EssarSignature.builder()
                        .signatureId(response.getSignatureId())
                        .uuid(uuid)
                        .transactionId(transactionId)
                        .userName(userName)
                        .tin(tin)
                        .formType(formType)
                        .appName(appName)
                        .intentId(intentId)
                        .signatureDate(LocalDateTime.now())
                        .signatureStatus(response.getStatus())
                        .signatureValue(response.getSignatureValue())
                        .build())
                .toFuture();
    }

    private record EssarSignatureRequest(
            String uuid,
            String transactionId,
            String userName,
            String tin,
            String formType,
            String appName,
            String intentId) {}

    private record EssarSignatureResponse(
            String signatureId,
            String status,
            String signatureValue) {}
} 