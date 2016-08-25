package com.fishbeans;

/**
 * Created by ubu on 8/19/16.
 */
public interface ConfigTrait {
    int DEFAULT_ADB_PORT = 5037;
    String DEFAULT_ENCODING = "ISO-8859-1";
    String DEFAULT_ADB_HOST = "127.0.0.1";
    String baseStoragePath = System.getProperty("user.dir");
    String outFilePath = "/alogs/";
    String adb_relative_path = "/platform-tools/adb";





}
