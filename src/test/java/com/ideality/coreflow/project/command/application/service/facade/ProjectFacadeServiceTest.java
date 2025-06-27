package com.ideality.coreflow.project.command.application.service.facade;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.dto.RequestModifyTaskDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.NodeDTO;
import com.ideality.coreflow.template.query.dto.NodeDataDTO;
import com.ideality.coreflow.template.query.dto.TemplateDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@SpringBootTest
@Transactional
class ProjectFacadeServiceTest {

    @BeforeAll
    static void setup() {
        TenantContext.setTenant("company_a");
    }

    @Autowired
    private ProjectFacadeService projectFacadeService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WorkRepository workRepository;

    @DisplayName("프로젝트 생성 테스트 (템플릿 X)")
    @Test
    void createProjectTest_NoTemplate() {
        // given
        ProjectCreateRequest request = new ProjectCreateRequest().builder()
                .name("프로젝트 생성 테스트 코드")
                .description("프로젝트 생성 테스트 코드 입니다.")
                .startBase(LocalDate.of(2025, 6, 1))
                .endBase(LocalDate.of(2025, 6, 5))
                .directorId(1L)
                .leaderIds(List.of(2L, 3L, 5L))
                .build();

        Project created = projectFacadeService.createProject(request);

        // then
        log.info("created project {}", created);
        assertNotNull(created.getId());
        Project found = projectRepository.findById(created.getId()).orElseThrow();
        assertEquals("프로젝트 생성 테스트 코드", found.getName());
    }

    @DisplayName("프로젝트 생성 테스트 (템플릿 O)")
    @Test
    void createProjectTest_WithTemplate() {
        TenantContext.setTenant("company_a");
        // given
        ProjectCreateRequest request = new ProjectCreateRequest().builder()
                .name("템플릿 적용 프로젝트 생성 테스트")
                .description("템플릿 적용 프로젝트 생성 테스트입니다.")
                .startBase(LocalDate.of(2025, 6, 1))
                .endBase(LocalDate.of(2025, 6, 30))
                .endExpect(LocalDate.of(2025, 6, 30))
                .directorId(1L)
                .leaderIds(List.of(2L, 3L))
                .templateId(1L)
                .templateData(
                        new TemplateDataDTO(
                                List.of(
                                        new NodeDTO(
                                                "1",
                                                "custom",
                                                new NodeDataDTO(
                                                        "label1",
                                                        "description1",
                                                        List.of(new TaskDeptDTO(1L, "기획팀")),
                                                        0,
                                                        "2025-06-01",
                                                        "2025-06-05"
                                                )
                                        ),
                                        new NodeDTO(
                                                "2",
                                                "custom",
                                                new NodeDataDTO(
                                                        "label2",
                                                        "description2",
                                                        List.of(new TaskDeptDTO(2L, "개발팀")),
                                                        0,
                                                        "2025-06-06",
                                                        "2025-06-10"
                                                )
                                        ),
                                        new NodeDTO(
                                                "3",
                                                "custom",
                                                new NodeDataDTO(
                                                        "label3",
                                                        "description3",
                                                        List.of(new TaskDeptDTO(2L, "개발팀"), new TaskDeptDTO(1L, "기획팀")),
                                                        0,
                                                        "2025-06-11",
                                                        "2025-06-25"
                                                )
                                        )

                                ),
                                List.of(
                                        new EdgeDTO(
                                                "e1-2",
                                                "1",
                                                "2",
                                                "default"
                                        ),
                                        new EdgeDTO(
                                                "e2-3",
                                                "2",
                                                "3",
                                                "default"
                                        )
                                )
                        )
                )
                .build();

        Project created = projectFacadeService.createProject(request);
        TenantContext.setTenant("company_a");

        // then
        log.info("created project {}", created);
        assertNotNull(created.getId());
    }

    @DisplayName("태스크 생성 테스트")
    @Test
    void createTask() {

        RequestTaskDTO requestTaskDTO = new RequestTaskDTO().builder()
                .label("태스크 생성 테스트")
                .description("태스크 생성 테스트입니다.")
                .startBaseLine(LocalDate.of(2025, 6, 2))
                .endBaseLine(LocalDate.of(2025, 6, 4))
                .projectId(1L)
                .deptList(List.of("기획팀"))
                .source(List.of(1L, 2L))
                .target(List.of(3L))
                .build();

        long taskId = projectFacadeService.createTask(requestTaskDTO, 1L);

        assertNotNull(taskId);
        Work found = workRepository.findById(taskId).orElseThrow();
        assertEquals(taskId, found.getId());
    }

    @DisplayName("세부일정 생성 테스트")
    @Test
    void createDetail() {
        RequestDetailDTO request = new RequestDetailDTO().builder()
                .projectId(1L)
                .parentTaskId(4L)
                .name("세부일정 테스트")
                .description("세부일정 테스트입니다.")
                .startBase(LocalDate.of(2025, 5, 30))
                .endBase(LocalDate.of(2025, 6, 5))
                .deptId(1L)
                .Source(null)
                .Target(List.of(6L, 7L))
                .assigneeId(1L)
                .participantIds(List.of(2L, 3L))
                .build();

        Long detailId = projectFacadeService.createDetail(request, 1L);

        assertNotNull(detailId);
        Work found = workRepository.findById(detailId).orElseThrow();
        assertEquals(detailId, found.getId());
    }

    @Test
    void updateDetail() {

        RequestDetailDTO request = new RequestDetailDTO().builder()
                .name("세부일정 수정 테스트")
                .description("세부일정 수정 테스트 입니다.")
                .deptId(3L)
                .assigneeId(5L)
                .participantIds(List.of(2L, 6L))
                .expectEnd(LocalDate.of(2025, 6, 28))
                .progress(23.4)
                .build();
        long detailId = projectFacadeService.updateDetail(5L, request);
        Work found = workRepository.findById(5L).orElseThrow();
        assertEquals(detailId, found.getId());
    }

    @DisplayName("태스크 수정 테스트")
    @Test
    void updateTaskDetail() {
        RequestModifyTaskDTO request = new RequestModifyTaskDTO().builder()
                .taskId(2L)
                .projectId(1L)
                .taskName("태스크 수정 테스트")
                .description("테스크 수정 테스트입니다.")
                .deptLists(List.of("기획팀", "디자인팀"))
                .prevTaskList(List.of(1L))
                .nextTaskList(List.of(3L, 4L))
                .startExpect(LocalDate.of(2025, 6, 3))
                .endExpect(LocalDate.of(2025, 6, 8))
                .build();

        long taskId = projectFacadeService.updateTaskDetail(request, 1L, 2L);
        Work found = workRepository.findById(2L).orElseThrow();
        assertEquals(taskId, found.getId());
    }
}