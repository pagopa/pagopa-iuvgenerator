package it.gov.pagopa.iuvgenerator.config;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataTableConfig {

    @Bean
    public TableClient tableClientConfiguration(@Value("${azure.tables.connection-string}") String connectionString,
                                                @Value("${azure.tables.table-name}") String tableName) {
        return new TableClientBuilder()
                .connectionString(connectionString)
                .tableName(tableName)
                .buildClient();
    }
}
