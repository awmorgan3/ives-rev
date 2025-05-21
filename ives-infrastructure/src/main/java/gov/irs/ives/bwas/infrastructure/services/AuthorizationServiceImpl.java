package gov.irs.ives.bwas.infrastructure.services;

import gov.irs.ives.bwas.core.domain.AuthorizationDocument;
import gov.irs.ives.bwas.core.ports.AuthorizationRepository;
import gov.irs.ives.bwas.core.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthorizationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorizationDocument> getDocuments(String tin, int page) {
        return repository.findByTin(tin, page);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorizationDocument getDocument(String transactionId, String tin, String tinType) {
        return repository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    @Override
    @Transactional
    public AuthorizationDocument authorize(String action, String transactionId, String documentTin,
            String tinType, String userId, String userTin) {
        AuthorizationDocument document = getDocument(transactionId, documentTin, tinType);
        
        document.setAuthorizationStatus(action);
        document.setUpdatedDate(LocalDateTime.now());
        
        return repository.save(document);
    }
} 