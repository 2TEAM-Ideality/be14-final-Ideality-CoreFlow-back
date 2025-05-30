package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.HolidayQueryDto;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HolidayQueryMapper {
    List<HolidayQueryDto> findByYear(@Param("year") int year);

    boolean existsByDate(@Param("date") LocalDate date);

    List<HolidayQueryDto> findByMonth(@Param("year") int year, @Param("month") int month);

    List<HolidayQueryDto> findBetweenDates(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
