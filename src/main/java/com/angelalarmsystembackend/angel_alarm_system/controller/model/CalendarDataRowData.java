package com.angelalarmsystembackend.angel_alarm_system.controller.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarDataRowData {
    private String month;
    private String day;
    private String year;
    private String description;
    private String time;
    private String dayOfTheWeek;
    private Integer key;
    boolean active;
}

//   month:string;
//           day:string;
//           year:string;
//           description:string;
//           time:string;
//           dayOfTheWeek: string;
//           key:number;
//           active?:boolean;