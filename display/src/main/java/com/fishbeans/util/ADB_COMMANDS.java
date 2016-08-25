package com.fishbeans.util;

/**
 * Created by ubu on 8/19/16.
 */
public enum ADB_COMMANDS {

    START_ADB_SERVER("start-server"),
    STOP_ADB_SERVER("kill-server"),
    SET_DEVICE_CMD("host:transport:%s"),
    LOGCAT_CMD("shell:logcat -v long"),
    LIST_DEVICES_CMD("host:devices"),
    SET_NO_FILTER(""),
    LIST_PACKAGES_CMD("shell:pm list packages");

    private String command;

    ADB_COMMANDS(String command) {
        this.command = command;
    }

    public String getCommand( ) {
        return command;
    }


    public static String setDevice(  String deviceId) {
        return String.format( SET_DEVICE_CMD.getCommand(), deviceId);
    }
}
