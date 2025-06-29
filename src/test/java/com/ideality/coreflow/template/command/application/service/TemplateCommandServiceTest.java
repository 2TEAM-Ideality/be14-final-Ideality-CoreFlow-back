package com.ideality.coreflow.template.command.application.service;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.template.command.application.dto.RequestCreateTemplateDTO;
import com.ideality.coreflow.template.command.domain.aggregate.Template;
import com.ideality.coreflow.template.command.domain.repository.TemplateDeptRepository;
import com.ideality.coreflow.template.command.domain.repository.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class TemplateCommandServiceTest {
    private TemplateRepository templateRepository;
    private TemplateDeptRepository templateDeptRepository;
    private TemplateCommandService templateCommandService;

    @BeforeEach
    void setUp() {
        templateRepository = mock(TemplateRepository.class);
        templateDeptRepository = mock(TemplateDeptRepository.class);
        templateCommandService = new TemplateCommandService(templateRepository, templateDeptRepository);
    }

    @Test
    void createTemplate_성공() {
        // given
        RequestCreateTemplateDTO dto = RequestCreateTemplateDTO.builder()
                .name("Test Template")
                .description("Description")
                .createdBy(1L)
                .duration(5)
                .taskCount(10)
                .build();

        // when
        Template result = templateCommandService.createTemplate(dto);

        // then
        // save 호출만 검증
        verify(templateRepository).save(any(Template.class));
    }


    @Test
    void updateTemplateInfo_성공() {
        // given
        Long templateId = 1L;
        Template existing = mock(Template.class);
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(existing));

        // when
        templateCommandService.updateTemplateInfo(templateId, "New Name", "New Desc", 10, 5, 2L);

        // then
        verify(existing).updateTemplate("New Name", "New Desc", 10, 5, 2L);
    }

    @Test
    void updateTemplateInfo_템플릿없으면_예외() {
        // given
        Long templateId = 1L;
        when(templateRepository.findById(templateId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> templateCommandService.updateTemplateInfo(templateId, "n", "d", 1, 1, 1L))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.TEMPLATE_NOT_FOUND.getMessage());
    }

    @Test
    void deleteTemplate_성공() {
        // given
        Long templateId = 1L;
        Template existing = mock(Template.class);
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(existing));

        // when
        templateCommandService.deleteTemplate(templateId);

        // then
        verify(existing).deleteTemplate();
    }

    @Test
    void saveTemplateDept_성공() {
        // given
        Long templateId = 1L;
        Long deptId = 100L;

        // when
        templateCommandService.saveTemplateDept(templateId, deptId);

        // then
        verify(templateDeptRepository).save(any());
    }


    @Test
    void deleteAllTemplateDepts_성공() {
        // given
        Long templateId = 1L;

        // when
        templateCommandService.deleteAllTemplateDepts(templateId);

        // then
        verify(templateDeptRepository).deleteByTemplateId(templateId);
    }
}
