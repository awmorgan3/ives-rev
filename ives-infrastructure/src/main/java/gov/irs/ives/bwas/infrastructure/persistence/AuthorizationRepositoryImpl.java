package gov.irs.ives.bwas.infrastructure.persistence;

import gov.irs.ives.bwas.core.domain.AuthorizationDocument;
import gov.irs.ives.bwas.core.ports.AuthorizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthorizationRepositoryImpl implements AuthorizationRepository {

    private final JpaAuthorizationRepository jpaRepository;

    @Override
    public List<AuthorizationDocument> findByTin(String tin, int page) {
        return jpaRepository.findByTinOrderByCreatedDateDesc(tin);
    }

    @Override
    public Optional<AuthorizationDocument> findByTransactionId(String transactionId) {
        return jpaRepository.findById(transactionId);
    }

    @Override
    public AuthorizationDocument save(AuthorizationDocument document) {
        return jpaRepository.save(document);
    }

    @Override
    public void delete(String transactionId) {
        jpaRepository.deleteById(transactionId);
    }
} 