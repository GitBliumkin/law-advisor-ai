package com.shared.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
public class JsonFactory {

    private final String mockDataPath = "mock-data";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public JsonFactory(String mockDataPath) {
//        this.mockDataPath = mockDataPath;
    }

    public <T> Optional<List<T>> getMockTableEntries(String tableName, Class<T> type) {
        return loadJsonList(mockDataPath + "/table-data/" + tableName + ".json", type);
    }

    public <T> Optional<T> getMockRequest(String tableName, Class<T> type, String requestName) throws Exception {
        if (requestName == null) {
            throw new Exception("Mock request name was not provided");
        }
        return loadJsonObject(mockDataPath + "/mock-requests/" + tableName + "-requests.json", type, requestName);
    }

    public <T> Optional<T> getMockResponse(String tableName, Class<T> type, String responseName) throws Exception {
        if (responseName == null) {
            throw new Exception("Mock response name was not provided");
        }
        return loadJsonObject(mockDataPath + "/mock-responses/" + tableName + "-responses.json", type, responseName);
    }

    private <T> Optional<List<T>> loadJsonList(String filePath, Class<T> type) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return Optional.of(objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, type)));
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }

    private <T> Optional<T> loadJsonObject(String filePath, Class<T> type, String entryName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JsonNode rootNode = objectMapper.readTree(content);

            if (rootNode.has(entryName)) {
                return Optional.of(objectMapper.treeToValue(rootNode.get(entryName), type));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }
}
