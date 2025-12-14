package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddImageRequest extends AASData{
    private String imageDataUrl;
}
