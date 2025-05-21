package gov.irs.ives.bwas.core.domain;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class EssarSignature {
    private String signatureId;
    private String uuid;
    private String transactionId;
    private String userName;
    private String tin;
    private String formType;
    private String appName;
    private String intentId;
    private LocalDateTime signatureDate;
    private String signatureStatus;
    private String signatureValue;
} 