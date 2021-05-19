/*
 *  The lBole licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.itaddr.common.tools.beans;

import com.itaddr.common.tools.constants.IntegerValue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @Author 马嘉祺
 * @Date 2019/11/1 0001 15 20
 * @Description <p></p>
 */
public class TimeStamp {
    
    private static final ZoneOffset EAST_EIGHTH_DISTRICT = ZoneOffset.of("+8");
    
    private static final String DATE_STRING_FORMAT = "%d-%02d-%02d %02d:%02d:%02d.%03d";
    
    private static final String ILLEGAL_ERROR_STRING = "字符串格式的时间戳不能为空";
    
    private static final Pattern FORMAT_SPLIT_PATTERN = Pattern.compile("[- :.T]");
    
    private LocalDateTime datetime;
    
    private Long value;
    
    private int year, month, day, hour, minute, second, millis;
    
    private String fmtTimeStr;
    
    public TimeStamp() {
        this.datetime = LocalDateTime.now();
        this.value = datetime.toInstant(EAST_EIGHTH_DISTRICT).toEpochMilli();
        this.year = datetime.getYear();
        this.month = datetime.getMonthValue();
        this.day = datetime.getDayOfMonth();
        this.hour = datetime.getHour();
        this.minute = datetime.getMinute();
        this.second = datetime.getSecond();
        this.millis = datetime.getNano() / 1000000;
        this.fmtTimeStr = String.format(DATE_STRING_FORMAT, year, month, day, hour, minute, second, millis);
    }
    
    public TimeStamp(long value) {
        this.datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(value), EAST_EIGHTH_DISTRICT);
        this.value = value;
        this.year = datetime.getYear();
        this.month = datetime.getMonthValue();
        this.day = datetime.getDayOfMonth();
        this.hour = datetime.getHour();
        this.minute = datetime.getMinute();
        this.second = datetime.getSecond();
        this.millis = datetime.getNano() / 1000000;
        this.fmtTimeStr = String.format(DATE_STRING_FORMAT, year, month, day, hour, minute, second, millis);
    }
    
    public TimeStamp(String fmtTimeStr) {
        if (null == fmtTimeStr || fmtTimeStr.length() == 0) {
            throw new IllegalArgumentException(ILLEGAL_ERROR_STRING);
        }
        final String[] splits = FORMAT_SPLIT_PATTERN.split(fmtTimeStr);
        if (IntegerValue.SEVEN != splits.length) {
            return;
        }
        this.fmtTimeStr = fmtTimeStr;
        this.year = Integer.parseInt(splits[0], 10);
        this.month = Integer.parseInt(splits[1], 10);
        this.day = Integer.parseInt(splits[2], 10);
        this.hour = Integer.parseInt(splits[3], 10);
        this.minute = Integer.parseInt(splits[4], 10);
        this.second = Integer.parseInt(splits[5], 10);
        this.millis = Integer.parseInt(splits[6], 10);
    }
    
    public TimeStamp(int year, int month, int day, int hour, int minute, int second, int millis) {
        this.fmtTimeStr = String.format(DATE_STRING_FORMAT, year, month, day, hour, minute, second, millis);
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millis = millis;
    }
    
    public LocalDateTime datetime() {
        if (null == datetime) {
            this.datetime = LocalDateTime.of(year, month, day, hour, minute, second, millis * 1000000);
            this.value = datetime.toInstant(EAST_EIGHTH_DISTRICT).toEpochMilli();
        }
        return datetime;
    }
    
    public long timestamp() {
        if (null == value) {
            this.datetime = LocalDateTime.of(year, month, day, hour, minute, second, millis * 1000000);
            this.value = datetime.toInstant(EAST_EIGHTH_DISTRICT).toEpochMilli();
        }
        return value;
    }
    
    public int year() {
        return year;
    }
    
    public int month() {
        return month;
    }
    
    public int day() {
        return day;
    }
    
    public int hour() {
        return hour;
    }
    
    public int minute() {
        return minute;
    }
    
    public int second() {
        return second;
    }
    
    public int millis() {
        return millis;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeStamp timeStamp = (TimeStamp) o;
        return year == timeStamp.year &&
                month == timeStamp.month &&
                day == timeStamp.day &&
                hour == timeStamp.hour &&
                minute == timeStamp.minute &&
                second == timeStamp.second &&
                millis == timeStamp.millis;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, hour, minute, second, millis);
    }
    
    @Override
    public String toString() {
        return fmtTimeStr;
    }
    
}
