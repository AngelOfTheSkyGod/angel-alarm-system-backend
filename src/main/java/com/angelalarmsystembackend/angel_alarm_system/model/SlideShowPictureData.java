package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SlideShowPictureData {
    private String imageDataUrl;
    private String fileName;
}