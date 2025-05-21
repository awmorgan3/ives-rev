package gov.irs.ives.bwas.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void generateTransactionId_ShouldGenerateValidUUID() {
        String transactionId = Utility.generateTransactionId();
        assertNotNull(transactionId);
        assertTrue(transactionId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    void formatDateTime_WithValidDateTime_ShouldFormatCorrectly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 10, 30, 45, 123000000);
        String formatted = Utility.formatDateTime(dateTime);
        assertEquals("2024-03-15T10:30:45.123Z", formatted);
    }

    @Test
    void formatDateTime_WithNull_ShouldReturnNull() {
        assertNull(Utility.formatDateTime(null));
    }

    @Test
    void parseDateTime_WithValidString_ShouldParseCorrectly() {
        String dateTimeStr = "2024-03-15T10:30:45.123Z";
        LocalDateTime parsed = Utility.parseDateTime(dateTimeStr);
        assertNotNull(parsed);
        assertEquals(2024, parsed.getYear());
        assertEquals(3, parsed.getMonthValue());
        assertEquals(15, parsed.getDayOfMonth());
        assertEquals(10, parsed.getHour());
        assertEquals(30, parsed.getMinute());
        assertEquals(45, parsed.getSecond());
    }

    @Test
    void parseDateTime_WithInvalidString_ShouldReturnNull() {
        assertNull(Utility.parseDateTime("invalid-date"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "987654321"})
    void isValidTin_WithValidTIN_ShouldReturnTrue(String tin) {
        assertTrue(Utility.isValidTin(tin));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "12345678", "1234567890", "abcdefghi", null})
    void isValidTin_WithInvalidTIN_ShouldReturnFalse(String tin) {
        assertFalse(Utility.isValidTin(tin));
    }

    @ParameterizedTest
    @ValueSource(strings = {"F1040", "W2", "1099R", "SSA1099"})
    void isValidDocumentType_WithValidTypes_ShouldReturnTrue(String documentType) {
        assertTrue(Utility.isValidDocumentType(documentType));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "A", "INVALID_TYPE_123", null})
    void isValidDocumentType_WithInvalidTypes_ShouldReturnFalse(String documentType) {
        assertFalse(Utility.isValidDocumentType(documentType));
    }
} 