package it.gov.pagopa.iuvgenerator;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.PagedResponse;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.ListEntitiesOptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Iterator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableClient tableClient;

    @Test
    void shouldRespondOKtoHeartBeat() throws Exception {
        PagedIterable pagedIterable = mock(PagedIterable.class);
        Iterable iterable = mock(Iterable.class);
        Iterator iterator = mock(Iterator.class);
        when(tableClient.listEntities(any(ListEntitiesOptions.class), any(Duration.class), any(com.azure.core.util.Context.class))).thenReturn(pagedIterable);
        when(pagedIterable.iterableByPage()).thenReturn(iterable);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.next()).thenReturn(mock(PagedResponse.class));
        mockMvc.perform(get("/info")).andExpect(status().isOk());
    }

    @Test
    void shouldRespondRedirectToSwagger() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().is3xxRedirection());
    }
}