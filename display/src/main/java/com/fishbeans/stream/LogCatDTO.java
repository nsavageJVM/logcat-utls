package com.fishbeans.stream;

import com.android.ddmlib.LogCatHeader;
import com.android.ddmlib.LogCatMessage;
import com.android.ddmlib.LogCatTimestamp;
import com.android.ddmlib.LogLevel;
import com.fishbeans.util.LogLineColor;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ubu on 8/22/16.
 */
@Data
@RequiredArgsConstructor
public class LogCatDTO {

    private static String headerMessage_Delim = " ::: ";
    private final LogCatMessage mssg;

    private LogLevel logLevel;
    private HandyTime ht;

    private static final Pattern timePattern = Pattern.compile(
            "^(\\d\\d)-(\\d\\d)\\s(\\d\\d):(\\d\\d):(\\d\\d)\\.(\\d+)$");


    public String prettyPrint() {
        StringJoiner logger = new StringJoiner(" ");
        logger.add(processHeader()).add(mssg.getMessage()).add("\n");

        return logger.toString();
    }

    public String getLineDisplayColor() {

        return LogLineColor.getLineDisplayColor(logLevel);
    }

    public String processHeader() {
        StringJoiner logger = new StringJoiner(" ");
        LogCatHeader header = mssg.getHeader();
        logLevel = header.getLogLevel();

        Matcher matcher = timePattern.matcher(header.getTimestamp().toString());
        matcher.matches();
        int hour = Integer.parseInt(matcher.group(3));
        int minute = Integer.parseInt(matcher.group(4));
        int second = Integer.parseInt(matcher.group(5));
        ht = new HandyTime(hour, minute, second);

        logger.add(ht.prettyPrint()).add(logLevel.getStringValue());

        return logger.toString();
    }


    @Data
    @RequiredArgsConstructor
    class HandyTime {

        final int hour;
        final int minute;
        final int second;

        public String prettyPrint() {
            StringJoiner logger = new StringJoiner("-");
            logger.add(string(hour)).add(string(minute)).add(string(second));
            return logger.toString();
        }

        String string(int i) {
            return Integer.toString(i);
        }

    }


}
