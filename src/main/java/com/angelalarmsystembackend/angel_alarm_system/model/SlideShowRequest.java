package com.angelalarmsystembackend.angel_alarm_system.model;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class SlideShowRequest extends LoginDetails{
    int pageNumber;
}
