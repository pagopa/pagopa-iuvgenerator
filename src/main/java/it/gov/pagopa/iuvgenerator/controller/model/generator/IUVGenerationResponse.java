package it.gov.pagopa.iuvgenerator.controller.model.generator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IUVGenerationResponse {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "..", example = "584756566583")
    private String iuv;
}
