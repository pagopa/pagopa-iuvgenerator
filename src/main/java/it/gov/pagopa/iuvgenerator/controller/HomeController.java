package it.gov.pagopa.iuvgenerator.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.iuvgenerator.controller.model.AppInfoResponse;
import it.gov.pagopa.iuvgenerator.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Validated
@Tag(name = "Home", description = "Application info APIs")
@RequiredArgsConstructor
public class HomeController {

    private final HealthService healthService;

    @Value("${server.servlet.context-path}")
    String basePath;


    /**
     * @return redirect to Swagger page documentation
     */
    @Hidden
    @GetMapping("")
    public RedirectView home() {
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        return new RedirectView(basePath + "swagger-ui.html");
    }

    /**
     * Health Check
     *
     * @return ok
     */
    @Operation(summary = "Return OK if application is started", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Home"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AppInfoResponse.class))),
    })
    @GetMapping("/info")
    public AppInfoResponse healthCheck() {
        return this.healthService.healthCheck();
    }

}
