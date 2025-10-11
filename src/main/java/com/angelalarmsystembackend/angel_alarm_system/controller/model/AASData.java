package com.angelalarmsystembackend.angel_alarm_system.controller.model;

import lombok.*;

import java.util.List;

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
}
//  username: string;
//          password: string;
//          alarmData: AlarmDataRowData[];
//          calendarData: CalendarDataRowData[];
//          slideShowData: SlideShowPictureData[];