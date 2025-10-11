package com.angelalarmsystembackend.angel_alarm_system.controller.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SlideShowPictureData {
    private List<Integer> imageArray;
}

//    imageArray: number[];
