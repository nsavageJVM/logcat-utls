package com.fishbeans.service;

import com.fishbeans.util.UsbDevice;
import javafx.scene.control.TextArea;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.List;

/**
 * Created by ubu on 8/20/16.
 */
public interface ADBService {

    void init();
    void startADBServer();
    void stopADBServer();

    void  stopLogStream();
    void  startLogStream();


    String setDevice(boolean isDefault, String deviceId);
    List<UsbDevice> getDevices();
    void runLogCatScheduledService(InlineCssTextArea terminalOutput);


    void setFilter(String filterText);
}
