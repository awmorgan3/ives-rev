package gov.irs.ives.bwas.core.ports;

import gov.irs.ives.bwas.core.domain.AuthorizationDocument;
import java.util.List;
import java.util.Optional;

public interface AuthorizationRepository {
    List<AuthorizationDocument> findByTin(String tin, int page);
    Optional<AuthorizationDocument> findByTransactionId(String transactionId);
    AuthorizationDocument save(AuthorizationDocument document);
    void delete(String transactionId);
} 