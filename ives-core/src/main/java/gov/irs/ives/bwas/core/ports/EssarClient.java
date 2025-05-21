package gov.irs.ives.bwas.core.ports;

import gov.irs.ives.bwas.core.domain.EssarSignature;
import java.util.concurrent.CompletableFuture;

public interface EssarClient {
    /**
     * Generates an electronic signature from ESSAR with the given parameters.
     * 
     * @param uuid of the signer
     * @param transactionId of the signed document
     * @param userName of the signer
     * @param tin of the signer
     * @param formType type of document that is signed
     * @param appName name of the application making the signature request
     * @param intentId identifier for the associated intent statement
     * @return CompletableFuture containing the electronic signature from the ESSAR system
     */
    CompletableFuture<EssarSignature> getElectronicSignature(
            String uuid, String transactionId, String userName, String tin,
            String formType, String appName, String intentId);
} 