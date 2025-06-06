package com.ideality.coreflow.mention.service.impl;

import com.ideality.coreflow.mention.service.MentionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MentionServiceImpl implements MentionService {
    @Override
    public List<String> parseTarget(String mentionTarget) {
        if (mentionTarget == null || mentionTarget.isBlank()) {
            return null;
        }
        // "_" 기준으로 나눔, 0 : name 1 : job_rank_name 2: dept_name
        String[] parts = mentionTarget.split("_");
        // 결과를 리스트로 변환
        return List.of(parts);
    }
}