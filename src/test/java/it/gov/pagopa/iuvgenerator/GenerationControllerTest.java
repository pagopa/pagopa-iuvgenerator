package it.gov.pagopa.iuvgenerator;


import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.iuvgenerator.controller.model.generator.IUVGenerationResponse;
import it.gov.pagopa.iuvgenerator.exception.AppErrorCodeMessageEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class GenerationControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableClient tableClient;

    public static String getRequest(Integer auxDigit, Integer segregationCode) {
        return "{ \"auxDigit\": " + auxDigit + ", \"segregationCode\": " + segregationCode + "}";
    }

    @Test
    void test_ok() throws Exception {

        String organizationFiscalCode = "77777777777";
        int auxDigit = 3;
        int segregationCode = 48;

        doNothing().when(tableClient).createEntity(any(TableEntity.class));

        String url = "/organizations/" + organizationFiscalCode + "/iuv";
        MvcResult result = mockMvc.perform(post(url)
                        .content(getRequest(auxDigit, segregationCode))
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

        IUVGenerationResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), IUVGenerationResponse.class);

        assertNotNull(response);
        assertNotNull(response.getIuv());
    }

    @ParameterizedTest
    @CsvSource(value = {"null, 48", "3, null", "null, null"}, nullValues = "null")
    void test_badRequest(Integer auxDigit, Integer segregationCode) throws Exception {

        String organizationFiscalCode = "77777777777";
        String url = "/organizations/" + organizationFiscalCode + "/iuv";
        mockMvc.perform(post(url)
                        .content(getRequest(auxDigit, segregationCode))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_ko() throws Exception {

        String organizationFiscalCode = "77777777777";
        int auxDigit = 3;
        int segregationCode = 48;

        doThrow(TableServiceException.class).when(tableClient).createEntity(any(TableEntity.class));

        String url = "/organizations/" + organizationFiscalCode + "/iuv";
        MvcResult result = mockMvc.perform(post(url)
                        .content(getRequest(auxDigit, segregationCode))
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        ProblemDetail response = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertNotNull(response);
        assertNotNull(response.getProperties());
        assertEquals("IUVG-" + AppErrorCodeMessageEnum.GENERATION_MAX_RETRIES_REACHED.getCode(), response.getProperties().get("error-code"));
    }

    @Test
    void test_koOnInvalidClock() throws Exception {

        String organizationFiscalCode = "77777777777";
        int auxDigit = 3;
        int segregationCode = 48;

        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS)) {
            Instant instant = Instant.now(Clock.fixed(Instant.parse("1970-01-01T01:00:00Z"), ZoneId.of("UTC")));
            mockedStatic.when(Instant::now).thenReturn(instant);

            String url = "/organizations/" + organizationFiscalCode + "/iuv";
            MvcResult result = mockMvc.perform(post(url)
                            .content(getRequest(auxDigit, segregationCode))
                            .contentType("application/json"))
                    .andExpect(status().isInternalServerError())
                    .andReturn();

            ProblemDetail response = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

            assertNotNull(response);
            assertNotNull(response.getProperties());
            assertEquals("IUVG-" + AppErrorCodeMessageEnum.GENERATION_AUXDIGIT_ALGORITHM_INVALID_PATTERN.getCode(), response.getProperties().get("error-code"));
        }
    }

    @Test
    void test_koUnexpectedError() throws Exception {

        String organizationFiscalCode = "77777777777";
        int auxDigit = 3;
        int segregationCode = 48;

        doThrow(RuntimeException.class).when(tableClient).createEntity(any(TableEntity.class));

        String url = "/organizations/" + organizationFiscalCode + "/iuv";
        MvcResult result = mockMvc.perform(post(url)
                        .content(getRequest(auxDigit, segregationCode))
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        ProblemDetail response = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertNotNull(response);
        assertNotNull(response.getProperties());
        assertEquals("IUVG-" + AppErrorCodeMessageEnum.GENERIC_ERROR.getCode(), response.getProperties().get("error-code"));
    }
}
