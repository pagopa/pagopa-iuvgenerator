package it.gov.pagopa.iuvgenerator.service;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableServiceException;
import it.gov.pagopa.iuvgenerator.controller.model.generator.IUVGenerationRequest;
import it.gov.pagopa.iuvgenerator.controller.model.generator.IUVGenerationResponse;
import it.gov.pagopa.iuvgenerator.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.iuvgenerator.exception.AppException;
import it.gov.pagopa.iuvgenerator.service.algorithm.AuxDigitIUVGeneratorAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerationService {

    private final TableClient tableClient;

    @Value("${iuvgenerator.generation.max-retries}")
    private Integer maxRetries;

    public IUVGenerationResponse generateIUV(String organizationFiscalCode, IUVGenerationRequest request) {

        String iuv = null;
        int retries = 1;

        AuxDigitIUVGeneratorAlgorithm algorithm = AuxDigitIUVGeneratorAlgorithm.getInstance()
                .auxDigit(request.getAuxDigit())
                .segregationCode(request.getSegregationCode());

        boolean found = false;
        while (mustTryAgain(found, retries)) {

            log.debug(String.format("Generating IUV for organization [%s]. Retry: [%d]", organizationFiscalCode, retries));
            iuv = algorithm.generate();
            found = this.checkIUVUniqueness(organizationFiscalCode, iuv);
            retries++;
        }

        if (!found) {
            log.error(String.format("Impossible to generate a valid unique IUV in [%d] retries for organization [%s].", retries, organizationFiscalCode));
            throw new AppException(AppErrorCodeMessageEnum.GENERIC_ERROR);
        }

        return IUVGenerationResponse.builder()
                .iuv(iuv)
                .build();
    }

    private boolean mustTryAgain(boolean found, int retries) {
        return !found && retries <= this.maxRetries;
    }

    private boolean checkIUVUniqueness(String organizationFiscalCode, String iuv) {
        boolean isUnique = true;
        try {
            TableEntity iuvEntity = new TableEntity(organizationFiscalCode, iuv);
            this.tableClient.createEntity(iuvEntity);
        } catch (TableServiceException | IllegalArgumentException e) {
            log.warn(String.format("A IUV with value [%s] already exists for organization [%s].", iuv, organizationFiscalCode));
            isUnique = false;
        }
        return isUnique;
    }
}
