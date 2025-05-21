package gov.irs.ives.bwas.api.mappers;

import gov.irs.ives.bwas.api.dtos.AuthorizationDocumentDTO;
import gov.irs.ives.bwas.core.domain.AuthorizationDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthorizationDocumentMapper {
    AuthorizationDocumentMapper INSTANCE = Mappers.getMapper(AuthorizationDocumentMapper.class);

    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "tin", source = "tin")
    @Mapping(target = "tinType", source = "tinType")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "updatedDate", source = "updatedDate")
    @Mapping(target = "documentType", source = "documentType")
    @Mapping(target = "documentStatus", source = "documentStatus")
    @Mapping(target = "authorizationStatus", source = "authorizationStatus")
    AuthorizationDocumentDTO toDTO(AuthorizationDocument document);

    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "tin", source = "tin")
    @Mapping(target = "tinType", source = "tinType")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "updatedDate", source = "updatedDate")
    @Mapping(target = "documentType", source = "documentType")
    @Mapping(target = "documentStatus", source = "documentStatus")
    @Mapping(target = "authorizationStatus", source = "authorizationStatus")
    AuthorizationDocument toDomain(AuthorizationDocumentDTO dto);
} 