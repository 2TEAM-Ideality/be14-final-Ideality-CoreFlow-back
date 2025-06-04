package com.ideality.coreflow.project.query.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.template.query.dto.NodeDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {
	private final TaskMapper taskMapper;

	@Override
	public List<NodeDTO> getTasksByProjectId(Long projectId) {
		return List.of();
	}
}
