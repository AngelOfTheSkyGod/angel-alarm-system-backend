package com.angelalarmsystembackend.angel_alarm_system.model;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlarmDataRowData {
    private String time;
    private List<String> days;
    private String description;
    private Integer key;
    private String sound;
    private boolean active;
}
//    time: string;
//            days: string[];
//            description: string;
//            key: number;
//            sound: string;
//            active: boolean;