package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@Service
@SpringBootTest(classes = ProjectFacadeService.class)
public class ProjectFacadeServiceTest {
    @Mock
    private TaskService taskService;
    @Mock
    private DeptService deptService;
    @Mock
    private RelationService relationService;
    @Mock
    private WorkDeptService workDeptService;
    @Mock
    private DeptQueryService deptQueryService;
    @Mock
    private UserQueryService userQueryService;
    @Mock
    private ParticipantService participantService;

    @DisplayName("태스크 생성 성공 테스트")
    @Test
    void createTaskTest() {

        // given

        // when

        // then
    }

}
