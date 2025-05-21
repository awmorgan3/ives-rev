package gov.irs.ives.bwas.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class providing common functionality for the BWAS application.
 */
@Slf4j
@Component
public class Utility {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * Generates a unique transaction ID.
     *
     * @return A unique transaction ID string
     */
    public static String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Formats a LocalDateTime to the standard format.
     *
     * @param dateTime The date time to format
     * @return Formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
    }

    /**
     * Parses a date time string to LocalDateTime.
     *
     * @param dateTimeStr The date time string to parse
     * @return Parsed LocalDateTime or null if invalid
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, DATE_FORMATTER) : null;
        } catch (Exception e) {
            log.error("Error parsing date time: {}", dateTimeStr, e);
            return null;
        }
    }

    /**
     * Validates a TIN (Taxpayer Identification Number).
     *
     * @param tin The TIN to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTin(String tin) {
        if (tin == null || tin.trim().isEmpty()) {
            return false;
        }
        // Add TIN validation logic here
        return tin.matches("\\d{9}");
    }

    /**
     * Validates a document type.
     *
     * @param documentType The document type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDocumentType(String documentType) {
        if (documentType == null || documentType.trim().isEmpty()) {
            return false;
        }
        // Add document type validation logic here
        return documentType.matches("[A-Z0-9]{2,10}");
    }
} 