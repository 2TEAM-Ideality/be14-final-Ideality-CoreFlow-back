package com.ideality.coreflow.holiday.command.application.controller;

import com.ideality.coreflow.holiday.command.application.dto.RequestInsertHoliday;
import com.ideality.coreflow.holiday.command.application.service.HolidayService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    @PostMapping("/update")
    public ResponseEntity<Map<String,Object>> updateHoliday(@RequestParam int year) {
        int savedCount = holidayService.updateHolidays(year);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", year+"년 휴일 정보 업데이트 완료");

        Map<String, Object> data = new HashMap<>();
        data.put("holidays", savedCount);

        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertDummyHoliday(@RequestBody RequestInsertHoliday request) {
        holidayService.insertDummyHoliday(request);
        return ResponseEntity.ok("더미 공휴일 저장 완료");
    }
}

