package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class AddImageRequestPi0 extends AddImageRequest {
    private String pathName;
    private String imageDataUrl;
    private String fileName;
}
