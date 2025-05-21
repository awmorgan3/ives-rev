package gov.irs.ives.bwas.core.services;

import gov.irs.ives.bwas.core.domain.AuthorizationDocument;
import java.util.List;

public interface AuthorizationService {
    /**
     * Gets a paged list of authorization documents based on the given TIN.
     *
     * @param tin of the requester
     * @param page 0-based index of page of documents to return
     * @return a paged list of authorization documents
     */
    List<AuthorizationDocument> getDocuments(String tin, int page);

    /**
     * Gets an authorization document based on the given transaction ID.
     *
     * @param transactionId of the document to retrieve
     * @param tin associated to the requested document
     * @param tinType of the given tin
     * @return the authorization document
     */
    AuthorizationDocument getDocument(String transactionId, String tin, String tinType);

    /**
     * Authorizes (approves or rejects) a document by the given transaction ID.
     *
     * @param action an APPROVE or REJECT authorization decision
     * @param transactionId an identifier of the document
     * @param documentTin tin associated to the document
     * @param tinType type of tin associated to the document
     * @param userId id of user performing the authorization
     * @param userTin tin associated to the user
     * @return the updated authorization document
     */
    AuthorizationDocument authorize(String action, String transactionId, String documentTin,
            String tinType, String userId, String userTin);
} 