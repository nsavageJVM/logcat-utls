package com.fishbeans;

import com.fishbeans.util.UsbDevice;
import org.junit.Test;

import java.util.List;

/**
 * Created by ubu on 8/23/16.
 */
public class ADBDeviceListTest extends ADBServiceTestBase {
    List<UsbDevice> result;

    @Test
    public void getDeviceList() {
        try {

            adbService.stopADBServer();
            Thread.sleep(500);
            adbService.init();
            Thread.sleep(500);
            result = adbService.getDevices();
            result.stream().forEach(d -> System.out.println(d.prettyPrint()));
            adbService.stopADBServer();
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {

            adbService.init();
            Thread.sleep(500);
            adbService.setDevice(false, result.get(0).getSerial() );
            getLogData();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void getLogData() {

        adbService.runLogCatScheduledService(null);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adbService.stopADBServer();


    }

}
