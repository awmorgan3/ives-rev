package gov.irs.ives.bwas.core.ports;

import gov.irs.ives.bwas.core.domain.FBPDocument;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FBPClient {
    /**
     * Gets the FBP document corresponding to the given transaction ID.
     * 
     * @param transactionId of an FBP document to search by
     * @return CompletableFuture containing the FBP document
     */
    CompletableFuture<FBPDocument> getDocument(String transactionId);

    /**
     * Gets a list of FBP documents corresponding to the given TIN.
     * 
     * @param tin to search by
     * @return CompletableFuture containing the FBP documents
     */
    CompletableFuture<List<FBPDocument>> getDocuments(String tin);

    /**
     * Authorizes (approves or rejects) an FBP document based on the given parameters.
     * 
     * @param action APPROVE or REJECT authorization decision
     * @param transactionId of the FBP document to authorize
     * @param tin of the authorizer
     * @param signatureId optional signature when approving a request
     * @return CompletableFuture containing the updated FBP document
     */
    CompletableFuture<FBPDocument> authorize(String action, String transactionId, String tin, String signatureId);
} 