package com.fishbeans;

import com.android.ddmlib.LogCatTimestamp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ubu on 24.08.16.
 */
public class LCatTimeTest {

    static final String TestData = "08-24 19:46:18.257";


    private static final Pattern timePattern = Pattern.compile(
            "^(\\d\\d)-(\\d\\d)\\s(\\d\\d):(\\d\\d):(\\d\\d)\\.(\\d+)$");


    public static void main(String[] args) {
        Matcher matcher = timePattern.matcher(TestData);
        matcher.matches();
        int day = Integer.parseInt(matcher.group(2));
        int hour = Integer.parseInt(matcher.group(3));
        int minute = Integer.parseInt(matcher.group(4));
    }






}
