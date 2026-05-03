package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class AddImageRequest extends LoginDetails{
    private String imageDataUrl;
    private String fileName;
}
