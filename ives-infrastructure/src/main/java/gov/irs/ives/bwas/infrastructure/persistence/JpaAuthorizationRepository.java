package gov.irs.ives.bwas.infrastructure.persistence;

import gov.irs.ives.bwas.core.domain.AuthorizationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAuthorizationRepository extends JpaRepository<AuthorizationDocument, String> {
    List<AuthorizationDocument> findByTinOrderByCreatedDateDesc(String tin);
} 