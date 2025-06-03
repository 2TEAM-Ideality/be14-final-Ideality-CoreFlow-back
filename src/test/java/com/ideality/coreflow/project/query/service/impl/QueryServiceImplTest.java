package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.holiday.query.mapper.DeptMapper;
import com.ideality.coreflow.holiday.query.service.impl.DeptQueryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeptQueryServiceImplTest {

    @InjectMocks
    private DeptQueryServiceImpl deptQueryService;

    @Mock
    private DeptMapper deptMapper;

    @Test
    @DisplayName("부서명이 존재하면 ID를 반환한다")
    void shouldReturnDeptId_whenDeptNameExists() {
        // given
        String deptName = "기획";
        Long expectedId = 1L;
        when(deptMapper.findIdByDeptName(deptName)).thenReturn(Optional.of(expectedId));

        // when
        Long result = deptQueryService.findIdByName(deptName);

        // then
        assertEquals(expectedId, result);
    }

    @Test
    @DisplayName("부서명이 존재하지 않으면 예외를 던진다")
    void shouldThrowException_whenDeptNameDoesNotExist() {
        // given
        String deptName = "존재하지않는부서";
        when(deptMapper.findIdByDeptName(deptName)).thenReturn(Optional.empty());

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                deptQueryService.findIdByName(deptName)
        );

        // then
        assertEquals(ErrorCode.DEPARTMENT_NOT_FOUND, exception.getErrorCode());
    }
}