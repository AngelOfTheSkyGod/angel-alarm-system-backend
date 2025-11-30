package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AASData {
    private String username;
    private String password;
    private List<AlarmDataRowData> alarmData;
    private List<CalendarDataRowData> calendarData;
    private List<SlideShowPictureData> slideShowData;
    private String remoteAddr;
}