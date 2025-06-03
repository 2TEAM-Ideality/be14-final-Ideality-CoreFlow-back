package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.project.query.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/work")
public class WorkController {

    private final WorkService workService;

    // parent_task_id가 동일한 세부일정 이름 목록 조회
    //선행, 후행 목록 조회에서 선택된 일정 빼는 것은 프론트에서 처리
    @GetMapping("/detailList")
    public List<String> getSubTaskNames(@RequestParam Long parentTaskId) {
        return workService.getSubTaskNamesByParentTaskId(parentTaskId);
    }


}
