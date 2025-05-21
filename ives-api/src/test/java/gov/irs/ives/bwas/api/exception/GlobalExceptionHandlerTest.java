package gov.irs.ives.bwas.api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleWebClientResponseException_ShouldReturnCorrectResponse() {
        WebClientResponseException ex = new WebClientResponseException(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                null,
                null,
                null
        );

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                handler.handleWebClientResponseException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals("External service error", response.getBody().error());
        assertEquals("Bad Request", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleCompletionException_ShouldReturnCorrectResponse() {
        CompletionException ex = new CompletionException("Async operation failed", 
                new RuntimeException("Original error"));

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                handler.handleCompletionException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().status());
        assertEquals("Async operation error", response.getBody().error());
        assertEquals("Async operation failed", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleException_ShouldReturnCorrectResponse() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                handler.handleException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().status());
        assertEquals("Internal server error", response.getBody().error());
        assertEquals("Unexpected error", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }
} 