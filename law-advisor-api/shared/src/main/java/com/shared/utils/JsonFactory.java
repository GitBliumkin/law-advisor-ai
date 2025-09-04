package com.shared.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
public class JsonFactory {

    private String mockDataPath = "mock-data";
    
    public JsonFactory(String mockDataPath) {
    	this.mockDataPath = mockDataPath;
    }
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public <T> Optional<List<T>> getMockTableEntries(String tableName, Class<T> type) {
        String p = mockDataPath + "/table-data/" + tableName + "-data.json";
        return loadJsonListFromClasspath(p, type);
    }

    public <T> Optional<T> getMockRequest(String tableName, Class<T> type, String requestName) throws Exception {
        if (requestName == null) throw new Exception("Mock request name was not provided");
        String p = mockDataPath + "/mock-requests/" + tableName + "-requests.json";
        return loadJsonObjectFromClasspath(p, type, requestName);
    }

    public <T> Optional<T> getMockResponse(String tableName, Class<T> type, String responseName) throws Exception {
        if (responseName == null) throw new Exception("Mock response name was not provided");
        String p = mockDataPath + "/mock-responses/" + tableName + "-responses.json";
        return loadJsonObjectFromClasspath(p, type, responseName);
    }

    private <T> Optional<List<T>> loadJsonListFromClasspath(String path, Class<T> type) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return Optional.of(
                objectMapper.readValue(in, objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, type))
            );
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON classpath resource: " + path, e);
        }
    }

    private <T> Optional<T> loadJsonObjectFromClasspath(String path, Class<T> type, String entryName) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            return root.has(entryName)
                    ? Optional.of(objectMapper.treeToValue(root.get(entryName), type))
                    : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON classpath resource: " + path, e);
        }
    }
}
