package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AASData extends LoginDetails{
    private List<AlarmDataRowData> alarmData;
    private List<CalendarDataRowData> calendarData;
}