package com.fishbeans.util;

import com.android.ddmlib.LogLevel;
import javafx.scene.paint.Color;

/**
 * Created by ubu on 24.08.16.
 */
public enum LogLineColor {

    VERBOSE("lightcoral"),
    DEBUG("greenyellow"),
    INFO("lavenderblush"),
    WARN("crimson"),
    ERROR("orange"),
    ASSERT("silver");

    String colorAsString;

    LogLineColor(String colorAsString) {
        this.colorAsString = colorAsString;
    }

    public static String getLineDisplayColor(LogLevel level) {
        String color =  "darkviolet";
        for(LogLineColor value: LogLineColor.values()) {
            if (value.name().equals(level.name())) {
                color =  value.getColorAsString();
            }
        }
        return color;

    }


    public String getColorAsString() {
        return colorAsString;
    }
}
