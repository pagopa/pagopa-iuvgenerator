package it.gov.pagopa.iuvgenerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.iuvgenerator.controller.model.generator.IUVGenerationRequest;
import it.gov.pagopa.iuvgenerator.controller.model.generator.IUVGenerationResponse;
import it.gov.pagopa.iuvgenerator.service.GenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Generation", description = "IUV Generation APIs")
public class GenerationController {

    private final GenerationService generationService;

    @Operation(summary = "... without aux-digit", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Generation"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = IUVGenerationRequest.class))),
    })
    @PostMapping("/organizations/{organization-fiscal-code}/iuv")
    public IUVGenerationResponse generateIUV(@Parameter(description = "...", example = "77777777777") @PathVariable("organization-fiscal-code") String organizationFiscalCode,
                                             @Valid @RequestBody IUVGenerationRequest request) {
        return generationService.generateIUV(organizationFiscalCode, request);
    }
}
