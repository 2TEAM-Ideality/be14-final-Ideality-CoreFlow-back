package com.ideality.coreflow.user.query.service;

import java.util.List;

public interface UserQueryService {

    List<String> findGeneralRolesByUserId(Long userId);
}
