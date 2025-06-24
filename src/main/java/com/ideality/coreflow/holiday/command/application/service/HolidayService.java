// HolidayService.java
package com.ideality.coreflow.holiday.command.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.holiday.command.application.dto.HolidayApiResponse;
import com.ideality.coreflow.holiday.command.application.dto.RequestInsertHoliday;
import com.ideality.coreflow.holiday.command.domain.aggregate.Holiday;
import com.ideality.coreflow.holiday.command.domain.aggregate.HolidayType;
import com.ideality.coreflow.holiday.command.domain.aggregate.RepeatType;
import com.ideality.coreflow.holiday.command.domain.repository.HolidayRepository;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Value("${HOLIDAY_SECRET_KEY}")
    private String serviceKey;

    public int updateHolidays(int year) {
        URI url = UriComponentsBuilder
                .fromUriString("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
                .queryParam("ServiceKey", serviceKey)
                .queryParam("solYear", year)
                .queryParam("_type", "json")
                .queryParam("numOfRows", 100)
                .build(true)
                .toUri();
        ResponseEntity<String> response=restTemplate.getForEntity(url, String.class);
        try {
            HolidayApiResponse apiResponse = objectMapper.readValue(response.getBody(), HolidayApiResponse.class);
            HolidayApiResponse.HolidayItem[] items = apiResponse.getResponse()
                    .getBody()
                    .getItems()
                    .getItem();

            List<Holiday> holidays = new ArrayList<>();
            Set<LocalDate> holidayDates = new HashSet<>();

            for (HolidayApiResponse.HolidayItem item : items) {
                LocalDate date = LocalDate.parse(item.getLocdate(), DateTimeFormatter.BASIC_ISO_DATE);
                Holiday holiday = Holiday.builder()
                        .name(item.getDateName())
                        .date(date)
                        .type(HolidayType.NATIONAL)
                        .isRepeat(RepeatType.ONCE)
                        .build();

                holidays.add(holiday);
                holidayDates.add(date);
            }

            LocalDate start=LocalDate.of(year,1,1);
            LocalDate end=LocalDate.of(year,12,31);
            for(LocalDate date=start;!date.isAfter(end);date=date.plusDays(1)){
                DayOfWeek day=date.getDayOfWeek();
                if((day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)&&!holidayDates.contains(date)){
                    Holiday weekend=Holiday.builder()
                            .name(day==DayOfWeek.SATURDAY?"토요일":"일요일")
                            .date(date)
                            .type(day==DayOfWeek.SATURDAY?HolidayType.SATURDAY:HolidayType.SUNDAY)
                            .isRepeat(RepeatType.ONCE)
                            .build();
                    holidays.add(weekend);
                }
            }

            holidays.sort(Comparator.comparing(Holiday::getDate));
            List<Holiday> saved = holidayRepository.saveAll(holidays);
            int savedCount = saved.size();
            System.out.println("공휴일 " +savedCount+"개 저장 완료");
            return savedCount;

        } catch (Exception e) {
            System.out.println("에러 발생");
            return 0;
        }
    }

    @Transactional
    public void insertDummyHoliday(RequestInsertHoliday request) {
        Holiday dummy = Holiday.builder()
                .name(request.getName())
                .date(request.getDate())
                .type(request.getHolidayType())
                .isRepeat(request.getRepeatType())
                .build();
        Holiday saved=holidayRepository.save(dummy);
    }
}
