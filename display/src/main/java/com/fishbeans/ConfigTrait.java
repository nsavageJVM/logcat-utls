package com.fishbeans;

/**
 * Created by ubu on 8/19/16.
 */
public interface ConfigTrait {
    int DEFAULT_ADB_PORT = 5037;
    String DEFAULT_ENCODING = "ISO-8859-1";
    String DEFAULT_ADB_HOST = "127.0.0.1";
    String aHome = System.getenv("ANDROID_HOME");
    String mAdbOsLocation = String.format("%s%s", aHome, "/platform-tools/adb");

    String defaultPackageName = "medvis.patient";


}
