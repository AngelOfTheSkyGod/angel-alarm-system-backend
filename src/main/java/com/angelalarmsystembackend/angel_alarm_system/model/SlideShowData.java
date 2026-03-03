package com.angelalarmsystembackend.angel_alarm_system.model;
import lombok.*;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SlideShowData {
    private Integer imageCount;
    private List<SlideShowPictureData> imageList;
}
