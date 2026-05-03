package com.angelalarmsystembackend.angel_alarm_system.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
public class DeleteImageRequest extends LoginDetails {
    int[] imagesDeleted;
    Integer pageNumber;
}
