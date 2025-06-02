package com.ideality.coreflow.project.command.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("✅ 태스크 생성 성공")
    void createTask_success() throws Exception {
        RequestTaskDTO dto = RequestTaskDTO.builder()
                .taskName("도식화")
                .taskDescription("도식화입니다")
                .projectId(1L)  // 🔹 존재하는 projectId
                .startBase(LocalDate.of(2025, 6, 1))
                .endBase(LocalDate.of(2025, 12, 1))
                .deptName("기획")
                .prevWorkId(0L)
                .nextWorkId(null)
                .build();

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNumber());
    }
}
