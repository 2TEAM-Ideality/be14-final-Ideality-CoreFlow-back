package com.ideality.coreflow.detail.command.service;

import com.ideality.coreflow.detail.command.entity.Dept;
import com.ideality.coreflow.detail.command.entity.User;
import com.ideality.coreflow.detail.command.entity.Work;
import com.ideality.coreflow.detail.command.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DetailService {

    private final WorkRepository workRepository;
    private final DeptRepository deptRepository;
    private final UserRepository userRepository;

    @Autowired
    public DetailService(WorkRepository workRepository, DeptRepository deptRepository, UserRepository userRepository) {
        this.workRepository = workRepository;
        this.deptRepository = deptRepository;
        this.userRepository = userRepository;
    }

    // 클라이언트가 보내는 작업 및 부서, 사용자 정보를 기반으로 세부 일정을 생성
    public Work createWorkWithDeptAndParticipants(Long parentTaskId, String title, String description, Long deptId,
                                                  List<Long> participantIds, Date startBase, Date endBase,
                                                  Double progressRate) {
        // 부모 작업 조회
        Work parentTask = workRepository.findById(parentTaskId)
                .orElseThrow(() -> new RuntimeException("부모 작업이 존재하지 않습니다."));

        // 부서 조회
        Dept dept = deptRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("부서가 존재하지 않습니다."));

        // 참여자들 조회
        List<User> participants = userRepository.findAllById(participantIds);

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
