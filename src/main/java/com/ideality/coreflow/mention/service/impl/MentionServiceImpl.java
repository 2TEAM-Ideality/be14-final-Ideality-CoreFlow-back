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
        String[] parts = mentionTarget.split("_");
        return List.of(parts);
    }
}