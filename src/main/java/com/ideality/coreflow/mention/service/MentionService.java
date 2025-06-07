package com.ideality.coreflow.mention.service;

import java.util.List;

public interface MentionService {
    List<String> parseTarget(String mentionTarget);
}
