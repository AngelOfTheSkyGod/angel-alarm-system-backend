package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddImageRequest extends LoginDetails{
    private String imageDataUrl;
    private String fileName;
}
