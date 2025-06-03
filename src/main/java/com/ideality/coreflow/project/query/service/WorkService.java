package com.ideality.coreflow.project.query.service;


import java.util.List;

public interface WorkService {
    // parent_task_id가 동일한 세부일정 이름 목록을 반환
    List<String> getSubTaskNamesByParentTaskId(Long parentTaskId);
}
