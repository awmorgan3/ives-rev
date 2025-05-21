package gov.irs.ives.bwas.core.domain;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class FBPDocument {
    private String transactionId;
    private String tin;
    private String documentType;
    private String documentStatus;
    private String authorizationStatus;
    private String signatureId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String documentContent;
    private String metadata;
} 