package com.shared.basecrud.controllers;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.basecrud.dtos.BaseDto;
import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.basecrud.handlers.BaseHandler;
import com.shared.basecrud.tables.BaseTable;
import com.shared.mvc.advice.GlobalApiExceptionHandler;
import com.shared.mvc.advice.ResponseEnvelopeAdvice;
import com.shared.utils.JsonFactory;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Base MVC test against controllers that extend the new BaseController and return BaseResponse / BaseListResponse.
 * Subclasses should add @WebMvcTest(controllers = YourController.class) and @MockBean YourHandler.
 */
@Import({ResponseEnvelopeAdvice.class, GlobalApiExceptionHandler.class})
public abstract class BaseControllerTest<Request extends BaseRequest,
                                         Dto extends BaseDto,
                                         Table extends BaseTable,
                                         Controller extends BaseController<Request, Dto, Table>> extends BaseTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected BaseHandler<Request, Dto, Table> handler;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JsonFactory jsonFactory;

    protected List<Table> mockEntries;
    protected String tableName;

    private final Class<Table> tableClass;
    private final Class<Request> requestClass;
    private final Class<Dto> dtoClass;

    protected abstract String getBaseUrl();
    protected String serviceName;

    @SuppressWarnings("unchecked")
    public BaseControllerTest() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        this.requestClass = (Class<Request>) type.getActualTypeArguments()[0];
        this.dtoClass = (Class<Dto>) type.getActualTypeArguments()[1];
        this.tableClass = (Class<Table>) type.getActualTypeArguments()[2];
    }

    @Override
    @BeforeAll
    protected void prepareMockData() {
        try {
            Optional<List<Table>> dtoListOptional = jsonFactory.getMockTableEntries(tableName, tableClass);
            if (dtoListOptional.isEmpty()) {
                throw new RuntimeException("Failed to extract json with mock data for table: " + tableName);
            }
            mockEntries = dtoListOptional.get();
            handler.saveRows(mockEntries);
        } catch (Exception e) {
            throw new RuntimeException("Error setting up mock data", e);
        }
    }

    @Override
    @AfterAll
    protected void cleanupMockData() {
        // handler.deleteMany(mockEntries);
    }

    protected Request getMockRequest(String testKeyCamel) throws Exception {
        Optional<Request> dto = jsonFactory.getMockRequest(tableName, requestClass, testKeyCamel);
        if (dto.isPresent()) return dto.get();
        String snake = toSnake(testKeyCamel);
        dto = jsonFactory.getMockRequest(tableName, requestClass, snake);
        return dto.orElseThrow(() -> new RuntimeException("Mock request not found: " + testKeyCamel + " (or " + snake + ")"));
    }

    protected Dto getMockResponse(String testKeyCamel) throws Exception {
        Optional<Dto> dto = jsonFactory.getMockResponse(tableName, dtoClass, testKeyCamel);
        if (dto.isPresent()) return dto.get();
        String snake = toSnake(testKeyCamel);
        dto = jsonFactory.getMockResponse(tableName, dtoClass, snake);
        return dto.orElseThrow(() -> new RuntimeException("Mock response not found: " + testKeyCamel + " (or " + snake + ")"));
    }

    private String toSnake(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    private Map<String, Object> pageMap(List<Dto> data, int size, int page, int totalCount, int totalPages) {
        Map<String, Object> m = new HashMap<>();
        m.put("data", data);
        m.put("size", size);
        m.put("page", page);
        m.put("totalCount", totalCount);
        m.put("totalPages", totalPages);
        return m;
    }

    private Dto newDtoWithId(String id) {
        try {
            Dto dto = dtoClass.getDeclaredConstructor().newInstance();
            try {
                Method setId = dtoClass.getMethod("setId", String.class);
                setId.invoke(dto, id);
            } catch (NoSuchMethodException ignored) {}
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate dto " + dtoClass.getName(), e);
        }
    }

    @Test
    void baseGetAllHappyPath() throws Exception {
        Dto dto = getMockResponse("baseGetAllHappyPath");
        List<Dto> data = List.of(dto);
        when(handler.getAll(2, 0)).thenReturn(pageMap(data, 2, 0, 1, 1));

        mockMvc.perform(get(getBaseUrl()).param("page", "0").param("size", "2"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.serviceName").value(serviceName))
               .andExpect(jsonPath("$.payload.size").value(2))
               .andExpect(jsonPath("$.payload.page").value(0))
               .andExpect(jsonPath("$.payload.totalCount").value(1))
               .andExpect(jsonPath("$.payload.totalPages").value(1))
               .andExpect(jsonPath("$.payload.data.length()").value(1))
               .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void baseGetAllHandlerThrows_returns500_withErrorEnvelope() throws Exception {
        when(handler.getAll(-1, -1)).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get(getBaseUrl()))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.serviceName").value(serviceName))
               .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"))
               .andExpect(jsonPath("$.error.message", not(emptyOrNullString())));
    }

    @Test
    void baseGetByIdHappyPath() throws Exception {
        Dto dto = getMockResponse("baseGetByIdHappyPath");
        when(handler.getById("42")).thenReturn(dto);

        mockMvc.perform(get(getBaseUrl() + "/42"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.serviceName").value(serviceName))
               .andExpect(jsonPath("$.payload").exists())
               .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void baseGetByIdHandlerThrows_badRequest400() throws Exception {
        when(handler.getById("x")).thenThrow(new IllegalArgumentException("not found"));

        mockMvc.perform(get(getBaseUrl() + "/x"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
               .andExpect(jsonPath("$.error.message", containsStringIgnoringCase("not found")));
    }

    @Test
    void baseCreateHappyPath_returns201() throws Exception {
        Request req = getMockRequest("baseCreateHappyPath");
        Dto respDto = getMockResponse("baseCreateHappyPath");
        when(handler.save(any())).thenReturn(respDto);

        mockMvc.perform(post(getBaseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.serviceName").value(serviceName))
               .andExpect(jsonPath("$.payload").exists())
               .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void baseCreateHandlerThrows_validation400() throws Exception {
        Request req = getMockRequest("baseCreateHappyPath");
        when(handler.save(any())).thenThrow(new IllegalArgumentException("validation failed"));

        mockMvc.perform(post(getBaseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
               .andExpect(jsonPath("$.error.message", containsStringIgnoringCase("validation")));
    }

    @Test
    void baseUpdateHappyPath_returns200() throws Exception {
        Request req = getMockRequest("baseUpdateHappyPath");
        Dto respDto = getMockResponse("baseUpdateHappyPath");
        when(handler.save(any())).thenReturn(respDto);

        mockMvc.perform(put(getBaseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.payload").exists());
    }

    @Test
    void baseUpdateHandlerThrows_badRequest400() throws Exception {
        Request req = getMockRequest("baseUpdateHappyPath");
        when(handler.save(any())).thenThrow(new IllegalArgumentException("conflict"));

        mockMvc.perform(put(getBaseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
               .andExpect(jsonPath("$.error.message", containsStringIgnoringCase("conflict")));
    }

    @Test
    void baseDeleteHappyPath_returns204_whenNoPayload() throws Exception {
        doNothing().when(handler).softDelete("9");

        mockMvc.perform(delete(getBaseUrl() + "/9"))
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$.success").value(true))
               .andExpect(jsonPath("$.serviceName").value(serviceName))
               .andExpect(jsonPath("$.payload").doesNotExist())
               .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void baseDeleteHandlerThrows_returns500_withErrorEnvelope() throws Exception {
        doThrow(new RuntimeException("cannot delete")).when(handler).softDelete("bad");

        mockMvc.perform(delete(getBaseUrl() + "/bad"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.success").value(false))
               .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"))
               .andExpect(jsonPath("$.error.message", not(emptyOrNullString())));
    }
}
