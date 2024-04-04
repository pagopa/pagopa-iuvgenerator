package it.gov.pagopa.iuvgenerator.service.algorithm;

import it.gov.pagopa.iuvgenerator.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.iuvgenerator.exception.AppException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.regex.Pattern;

public class AuxDigitIUVGeneratorAlgorithm implements IUVGeneratorAlgorithm {

    private static final Pattern THIRTEEN_DIGITS_PATTERN = Pattern.compile("\\d{13}");

    private int auxDigit;

    private int segregationCode;

    public static AuxDigitIUVGeneratorAlgorithm getInstance() {
        return new AuxDigitIUVGeneratorAlgorithm();
    }

    public AuxDigitIUVGeneratorAlgorithm auxDigit(int auxDigit) {
        this.auxDigit = auxDigit;
        return this;
    }

    public AuxDigitIUVGeneratorAlgorithm segregationCode(int segregationCode) {
        this.segregationCode = segregationCode;
        return this;
    }

    @Override
    public String generate() {
        String segregationCodeString = new DecimalFormat("00").format(this.segregationCode);
        String iuvBase13Digits = generateIuv13Digits();
        String checkDigit = generateCheckDigit(this.auxDigit + segregationCodeString + iuvBase13Digits);
        return segregationCodeString + iuvBase13Digits + checkDigit;
    }

    /**
     * Calculates the check digit of IUV code
     *
     * @param checkDigitComponent check digit component
     * @return the generated check digit
     */
    protected String generateCheckDigit(String checkDigitComponent) {
        return String.format("%02d", (new BigDecimal(checkDigitComponent).remainder(new BigDecimal(93))).intValue());
    }

    /**
     * Generates 13 digits IUV
     *
     * @return the IUV base
     * @throws AppException
     */
    protected String generateIuv13Digits() {
        String sequence = Long.toString(Instant.now().toEpochMilli());
        if (!THIRTEEN_DIGITS_PATTERN.matcher(sequence).matches()) {
            throw new AppException(AppErrorCodeMessageEnum.GENERIC_ERROR);
        }
        return sequence;
    }
}
