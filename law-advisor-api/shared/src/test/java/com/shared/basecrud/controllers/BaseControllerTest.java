package com.shared.basecrud.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.basecrud.controllers.BaseController;
import com.shared.basecrud.dtos.BaseDto;
import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.handlers.BaseHandler;
import com.shared.basecrud.tables.BaseTable;
import com.shared.utils.JsonFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public abstract class BaseControllerTest<Request extends BaseRequest, 
                                         Dto extends BaseDto, 
                                         Table extends BaseTable, 
                                         Controller extends BaseController<Request, Dto, Table>> extends BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    protected BaseHandler<Request, Dto, Table> handler;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private JsonFactory jsonFactory;
    
    protected List<Table> mockEntries;
    protected String tableName;
    
    private final Class<Table> tableClass;
    private final Class<Request> requestClass;
    private final Class<Dto> dtoClass;

    protected abstract String getBaseUrl();

    @SuppressWarnings("unchecked")
    public BaseControllerTest() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        this.requestClass = (Class<Request>) type.getActualTypeArguments()[0];
        this.dtoClass = (Class<Dto>) type.getActualTypeArguments()[1];
        this.tableClass = (Class<Table>) type.getActualTypeArguments()[2];
    }

    @Override
//    @BeforeAll
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
//    @AfterAll
    protected void cleanupMockData() {
//        handler.deleteMany(mockEntries);
    }
    
    protected Request getMockRequest(String requestName) throws Exception {
        Optional<Request> dtoOptional = jsonFactory.getMockRequest(tableName, requestClass, requestName);
        return dtoOptional.orElseThrow(() -> new RuntimeException("Mock request not found: " + requestName));
    }
    
    protected Dto getMockResponse(String responseName) throws Exception {
        Optional<Dto> dtoOptional = jsonFactory.getMockResponse(tableName, dtoClass, responseName);
        return dtoOptional.orElseThrow(() -> new RuntimeException("Mock response not found: " + responseName));
    }
    
    @Test
    void test() throws Exception {
    	prepareMockData();
    }
}
