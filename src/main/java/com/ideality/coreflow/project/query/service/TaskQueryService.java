package com.ideality.coreflow.project.query.service;

import java.util.List;


import com.ideality.coreflow.template.query.dto.NodeDTO;

public interface TaskQueryService {
	List<NodeDTO> getTasksByProjectId(Long projectId);
}
