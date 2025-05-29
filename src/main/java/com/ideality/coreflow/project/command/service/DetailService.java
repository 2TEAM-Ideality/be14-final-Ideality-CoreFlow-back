package com.ideality.coreflow.project.command.service;

import com.ideality.coreflow.project.command.domain.aggregate.Dept;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import com.ideality.coreflow.user.command.domain.aggregate.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DetailService {

    private final WorkRepository workRepository;


    @Autowired
    public DetailService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    // 클라이언트가 보내는 작업 및 부서, 사용자 정보를 기반으로 세부 일정을 생성
    public Work createWorkWithDeptAndParticipants(Long parentTaskId, String title, String description, Long deptId,
                                                  List<Long> participantIds, Date startBase, Date endBase,
                                                  Double progressRate) {

        // 새로운 세부 작업 생성
        Work newWork = new Work();
        newWork.setName(title);
        newWork.setDescription(description);
        newWork.setStartBase(startBase);
        newWork.setEndBase(endBase);
        newWork.setProgressRate(progressRate);
        newWork.setDept(dept);
        newWork.setParentTask(parentTask);

        // 세부 작업 저장
        return workRepository.save(newWork);
    }
}