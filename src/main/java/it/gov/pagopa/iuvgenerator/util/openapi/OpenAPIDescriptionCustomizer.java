package it.gov.pagopa.iuvgenerator.util.openapi;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import it.gov.pagopa.iuvgenerator.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.iuvgenerator.util.CommonUtility;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenAPIDescriptionCustomizer implements OpenApiCustomizer {

    private static final String SEPARATOR = " | ";
    @Value("${info.application.name}")
    private String name;
    @Value("${info.application.version}")
    private String version;
    @Value("${info.application.description}")
    private String description;

    private static String buildErrorData() {
        StringBuilder builder = new StringBuilder("\n\n**STANDARD ERRORS:**\n");
        builder.append("NAME").append(SEPARATOR).append("CODE").append(SEPARATOR).append("DESCRIPTION").append("\n");
        builder.append("-").append(SEPARATOR).append("-").append(SEPARATOR).append("-").append("\n");

        for (AppErrorCodeMessageEnum errorCode : AppErrorCodeMessageEnum.values()) {

            String detail = errorCode.getDetail();
            detail = detail.replaceAll("(\\[\\{\\d+\\}\\])", " [*...content...*]");
            detail = detail.replaceAll("(\\{\\d+\\})", " *...error description...*");

            builder.append("**").append(CommonUtility.getAppCode(errorCode)).append("** ").append(SEPARATOR)
                    .append("*").append(errorCode.name()).append("*").append(SEPARATOR)
                    .append(detail).append("\n");
        }

        return builder.toString();
    }

    @Override
    public void customise(OpenAPI openApi) {

        openApi.info(new Info()
                .title(this.name)
                .version(this.version)
                .description(this.description + buildErrorData())
                .termsOfService("https://www.pagopa.gov.it/"));

        Server hostServer = new Server();
        hostServer.setUrl("${host}");
        hostServer.setDescription("Generated server URL");
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local server URL");
        openApi.servers(List.of(hostServer, localServer));

        openApi.getComponents().addSecuritySchemes("ApiKey", new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .description("The API key to access this function app.")
                .name("Ocp-Apim-Subscription-Key")
                .in(SecurityScheme.In.HEADER));
    }
}