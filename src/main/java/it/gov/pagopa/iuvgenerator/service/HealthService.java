package it.gov.pagopa.iuvgenerator.service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.PagedResponse;
import com.azure.core.util.Context;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableServiceException;
import it.gov.pagopa.iuvgenerator.controller.model.AppInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Iterator;

@Service
@Slf4j
@RequiredArgsConstructor
public class HealthService {

    private final TableClient tableClient;

    @Value("${info.application.name}")
    private String name;

    @Value("${info.application.version}")
    private String version;

    @Value("${info.properties.environment}")
    private String environment;

    public AppInfoResponse healthCheck() {

        String dbCheck = "ok";
        try {
            ListEntitiesOptions options = new ListEntitiesOptions();
            options.setTop(1);
            PagedIterable<TableEntity> entities = this.tableClient.listEntities(options, Duration.ofMinutes(1), Context.NONE);
            Iterator<PagedResponse<TableEntity>> iterator = entities.iterableByPage().iterator();
            iterator.next().getValue();
        } catch (TableServiceException | IllegalArgumentException e) {
            dbCheck = "ko";
        }

        return AppInfoResponse.builder()
                .name(name)
                .version(version)
                .environment(environment)
                .db(dbCheck)
                .build();
    }
}
