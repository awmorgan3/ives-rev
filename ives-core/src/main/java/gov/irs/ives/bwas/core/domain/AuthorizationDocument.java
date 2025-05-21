package gov.irs.ives.bwas.core.domain;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class AuthorizationDocument {
    private String transactionId;
    private String tin;
    private String tinType;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String documentType;
    private String documentStatus;
    private String authorizationStatus;
} 