package it.gov.pagopa.iuvgenerator.util;

import it.gov.pagopa.iuvgenerator.exception.AppErrorCodeMessageEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtility {

    /**
     * @param value value to deNullify.
     * @return return empty string if value is null
     */
    public static String deNull(Object value) {
        return Optional.ofNullable(value).orElse("").toString();
    }

    public static String getAppCode(AppErrorCodeMessageEnum error) {
        return String.format("%s-%s", Constants.SERVICE_CODE_APP, error.getCode());
    }


}
