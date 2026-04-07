package com.example.task_manager.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create and find task successfully")
    void shouldCreateAndFindTaskSuccessfully() throws Exception {
        String requestBody = """
                {
                  "title": "Study",
                  "description": "Hexagonal architecture"
                }
                """;

        String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Study"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(createResponse);
        Long id = jsonNode.get("id").asLong();

        assertNotNull(id);

        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Study"))
                .andExpect(jsonPath("$.description").value("Hexagonal architecture"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should complete task successfully")
    void shouldCompleteTaskSuccessfully() throws Exception {
        String requestBody = """
                {
                  "title": "Complete me",
                  "description": "Task to be completed"
                }
                """;

        String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(createResponse);
        Long id = jsonNode.get("id").asLong();

        mockMvc.perform(patch("/tasks/" + id + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should return conflict when completing an already completed task")
    void shouldReturnConflictWhenTaskIsAlreadyCompleted() throws Exception {
        String requestBody = """
                {
                  "title": "Already done",
                  "description": "Task to test conflict"
                }
                """;

        String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(createResponse);
        Long id = jsonNode.get("id").asLong();

        mockMvc.perform(patch("/tasks/" + id + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(patch("/tasks/" + id + "/complete"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/tasks/" + id + "/complete"));
    }

    @Test
    @DisplayName("Should return bad request when create task request is invalid")
    void shouldReturnBadRequestWhenCreateTaskRequestIsInvalid() throws Exception {
        String invalidRequestBody = """
                {
                  "title": "",
                  "description": "Invalid task"
                }
                """;

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/tasks"));
    }
}