package com.angelalarmsystembackend.angel_alarm_system.utils;

import com.angelalarmsystembackend.angel_alarm_system.model.WeatherGov.ApparentTemperatureData;
import com.angelalarmsystembackend.angel_alarm_system.model.WeatherGov.Period;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WeatherUtils {
    public static List<Integer> getLowAndMaxTemperature(List<Period> periodList){
        LocalDate currentDate = LocalDate.now(); // Create a date object
        List<Period> getTodayTempInfo = periodList.stream().filter((temp) ->
                        currentDate.getDayOfMonth() == (OffsetDateTime.parse(Arrays.stream(temp.getStartTime().split("/")).toList().get(0)).getDayOfMonth())).toList();

        AtomicInteger low = new AtomicInteger(100);
        AtomicInteger high = new AtomicInteger(-100);

        getTodayTempInfo.forEach((Period period) -> {
            if (Integer.parseInt(period.getTemperature()) > high.get()){
                high.set(Integer.parseInt(period.getTemperature()));
            }
            if (Integer.parseInt(period.getTemperature()) < low.get()){
                low.set(Integer.parseInt(period.getTemperature()));
            }
        });

        return List.of(low.get(), high.get());
    }

    public static Integer getFeelsLikeTemperature(List<ApparentTemperatureData> apparentTemperatures){
        LocalTime now = LocalTime.now();
        return (apparentTemperatures.stream().filter((apparentTemperatureData -> OffsetDateTime.parse(Arrays.stream(apparentTemperatureData.getValidTime().split("/")).toList().get(0)).getHour() == now.getHour())).toList().get(0).getValue()* 9/5) + 32;
    }
}
