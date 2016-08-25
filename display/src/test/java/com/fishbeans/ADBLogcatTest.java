package com.fishbeans;

import com.fishbeans.util.ADB_COMMANDS;
import org.junit.Test;

/**
 * Created by ubu on 8/23/16.
 */
public class ADBLogcatTest extends ADBServiceTestBase {


    @Test
    public void getLogData() {

        adbService.setDevice(true, "");
        adbService.setFilter(ADB_COMMANDS.SET_NO_FILTER.name());
        adbService.runLogCatScheduledService(null);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adbService.stopADBServer();


    }




}
