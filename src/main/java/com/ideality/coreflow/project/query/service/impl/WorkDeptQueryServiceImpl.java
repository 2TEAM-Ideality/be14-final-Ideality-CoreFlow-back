package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.project.query.dto.ResponseTaskDTO;
import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import com.ideality.coreflow.project.query.dto.WorkDeptDTO;
import com.ideality.coreflow.project.query.mapper.WorkDeptMapper;
import com.ideality.coreflow.project.query.service.WorkDeptQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkDeptQueryServiceImpl implements WorkDeptQueryService {

    private final WorkDeptMapper workDeptMapper;

    @Override
    public void selectDeptList(List<ResponseTaskDTO> tasks) {
        List<Long> taskIds = tasks.stream()
                .map(ResponseTaskDTO::getId)
                .toList();

        List<WorkDeptDTO> deptList = workDeptMapper.selectDeptListMap(taskIds);

        Map<Long, List<TaskDeptDTO>> taskDeptMap = deptList.stream()
                .collect(Collectors.groupingBy(
                        WorkDeptDTO::getTaskId,
                        Collectors.mapping(
                                d -> new TaskDeptDTO(d.getDeptId(), d.getDeptName()),
                                Collectors.toList()
                        )
                ));

        // task에 부서 정보 주입
        for (ResponseTaskDTO task : tasks) {
            List<TaskDeptDTO> depts = taskDeptMap.getOrDefault(task.getId(), new ArrayList<>());
            task.setDepts(depts);
        }
    }
}
