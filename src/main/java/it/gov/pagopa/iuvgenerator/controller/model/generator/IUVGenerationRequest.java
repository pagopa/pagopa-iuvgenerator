package it.gov.pagopa.iuvgenerator.controller.model.generator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IUVGenerationRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "The value of the AUX digit related to the IUV.", example = "3")
    private Integer auxDigit;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "The value of the segregation related to the IUV.", example = "48")
    private Integer segregationCode;
}
