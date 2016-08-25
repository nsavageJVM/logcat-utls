package com.fishbeans.app.ui.dialog;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Created by ubu on 24.08.16.
 */
public class DeviceOffLineAlert extends Dialog<Boolean> {

    private ButtonType quitButton;

    public DeviceOffLineAlert() {
        this.setTitle("Device Inactive State");
        this.setHeaderText("ADB reports your device is offline");

        quitButton = new ButtonType("QUIT", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(quitButton);

        this.setResultConverter(buttonType -> {
            if (buttonType == quitButton) {
                return true;
            }
            return false;
        });

    }
}
