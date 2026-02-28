package com.angelalarmsystembackend.angel_alarm_system.model;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SlideShowRequest extends LoginDetails{
    int pageNumber;
    int startNumber;
}
