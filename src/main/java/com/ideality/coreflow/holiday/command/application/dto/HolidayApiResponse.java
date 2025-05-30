// HolidayApiResponse.java
package com.ideality.coreflow.holiday.command.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayApiResponse {
    private Response response;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Response {
        private Header header;
        private Body body;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Body {
        private Items items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Items {
        private HolidayItem[] item;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class HolidayItem {
        private String dateName;  // 공휴일 이름
        private String locdate;   // 날짜 (yyyyMMdd)
    }
}
