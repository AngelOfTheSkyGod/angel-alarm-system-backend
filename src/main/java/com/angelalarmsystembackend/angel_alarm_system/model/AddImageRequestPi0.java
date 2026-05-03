package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddImageRequestPi0 extends AddImageRequest {
    private String pathName;
    private String imageDataUrl;
    private String fileName;
}
