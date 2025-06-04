package com.ideality.coreflow.org.command.application.service;

import com.ideality.coreflow.org.command.application.dto.ResponseJobRank;

import java.util.List;

public interface JobRankService {

    List<ResponseJobRank> findAllJobRank();

    void registJobRank(String name);

    void updateJobRank(String prevJobRankName, String newJobRankName);

    void deleteJobRank(long id);
}
