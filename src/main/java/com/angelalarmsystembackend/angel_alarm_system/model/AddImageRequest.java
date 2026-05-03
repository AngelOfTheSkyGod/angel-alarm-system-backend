package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddImageRequest extends LoginDetails{
    private String imageDataUrl;
    private String fileName;
}
