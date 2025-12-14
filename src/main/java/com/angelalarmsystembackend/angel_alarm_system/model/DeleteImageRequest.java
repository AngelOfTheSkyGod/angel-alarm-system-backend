package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeleteImageRequest extends LoginDetails {
    Integer imagePosition;
}
