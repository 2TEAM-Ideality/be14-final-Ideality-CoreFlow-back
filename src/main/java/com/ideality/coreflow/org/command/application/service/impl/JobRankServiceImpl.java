package com.ideality.coreflow.org.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.org.command.application.dto.ResponseJobRank;
import com.ideality.coreflow.org.command.application.service.JobRankService;
import com.ideality.coreflow.org.command.domain.aggregate.JobRank;
import com.ideality.coreflow.org.command.domain.repository.JobRankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobRankServiceImpl implements JobRankService {

    private final JobRankRepository jobRankRepository;

    @Override
    public List<ResponseJobRank> findAllJobRank() {
        List<JobRank> result = jobRankRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return result.stream()
                .map(jobRank -> new ResponseJobRank(jobRank.getId(), jobRank.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void registJobRank(String name) {
        JobRank jobRank = JobRank.builder().name(name).build();
        jobRankRepository.save(jobRank);
    }

    @Override
    public void deleteJobRank(long id) {
        jobRankRepository.deleteById(id);
    }

    @Override
    public void updateJobRank(String prevJobRankName, String newJobRankName) {
        JobRank jobRank = jobRankRepository.findByName(prevJobRankName).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));

        jobRank.updateNameFrom(newJobRankName);

        jobRankRepository.save(jobRank);
    }
}
