package gov.irs.ives.bwas.api.controllers;

import gov.irs.ives.bwas.api.dtos.AuthorizationDocumentDTO;
import gov.irs.ives.bwas.core.services.AuthorizationService;
import gov.irs.ives.bwas.api.mappers.AuthorizationDocumentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/authorizations")
@RequiredArgsConstructor
@Tag(name = "Authorization", description = "Authorization management APIs")
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final AuthorizationDocumentMapper mapper;

    @GetMapping
    @Operation(summary = "Get authorization documents by TIN")
    public ResponseEntity<List<AuthorizationDocumentDTO>> getDocuments(
            @RequestParam String tin,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(
            authorizationService.getDocuments(tin, page)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get authorization document by transaction ID")
    public ResponseEntity<AuthorizationDocumentDTO> getDocument(
            @PathVariable String transactionId,
            @RequestParam String tin,
            @RequestParam String tinType) {
        return ResponseEntity.ok(
            mapper.toDTO(authorizationService.getDocument(transactionId, tin, tinType))
        );
    }

    @PostMapping("/{transactionId}/authorize")
    @Operation(summary = "Authorize a document")
    public ResponseEntity<AuthorizationDocumentDTO> authorize(
            @PathVariable String transactionId,
            @RequestParam String action,
            @RequestParam String documentTin,
            @RequestParam String tinType,
            @RequestParam String userId,
            @RequestParam String userTin) {
        return ResponseEntity.ok(
            mapper.toDTO(authorizationService.authorize(
                action, transactionId, documentTin, tinType, userId, userTin))
        );
    }
} 