package com.fishbeans.app.ui.controllers;

import com.android.ddmlib.LogLevel;
import com.fishbeans.app.ui.controllers.view.components.TerminalView;
import com.fishbeans.service.ADBService;
import com.fishbeans.util.ADB_COMMANDS;
import com.fishbeans.util.LogLineColor;
import com.fishbeans.util.UsbDevice;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * https://dzone.com/articles/use-adb-over-wi-fi-without
 */
@Component
public class BasePanelController implements ViewController {

    private static final String DEFAULT_FILTER_TEST = "Current Filter %s";

    @FXML
    private VBox mainContainer;

    @FXML
    private TabPane tabPane;

    @FXML
    private TextField filterField;

    @FXML
    private Label filterDisplay;

    @FXML
    private ComboBox<UsbDevice> selectDeviceCombo;

    @FXML
    private ComboBox<String> selectLogLevel;


    @Autowired
    private ADBService adbService;

    @Autowired
    private TerminalView terminal;

    @Value("${lcat.default.filter}")
    private String filter;

    private InlineCssTextArea terminalOutput;
    private VirtualizedScrollPane<InlineCssTextArea > terminalOutputScrollPane;

    private static final boolean isDefault = true;

    @FXML
    public void initialize() {


        filterDisplay.setText(String.format(DEFAULT_FILTER_TEST, filter));
        tabPane.getTabs().add(terminal);
        terminalOutput=  terminal.getOutput();
        terminalOutput.getStyleClass().add("terminal");
        terminalOutputScrollPane = terminal.getScrollPane();
        ObservableList<String> levels = FXCollections.observableArrayList();
        for(LogLevel l :LogLevel.values()) {
            levels.add(l.getStringValue());
        }
        selectLogLevel.setItems(levels);

        List<UsbDevice> deviceResult = adbService.getDevices();
        ObservableList<UsbDevice> values = FXCollections.observableArrayList();
        values.addAll(deviceResult);
        selectDeviceCombo.setItems(values);

        try {
            adbService.stopADBServer();
            Thread.sleep(1000);
            adbService.init();
            Thread.sleep(500); } catch (InterruptedException e) {
            e.printStackTrace();
        }
        selectLogLevel.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue ov, String oldLevel, String newLevel) {
                adbService.setIsLogLevelFilter(true);
                System.out.println(newLevel);
                adbService.setLogLevelString(newLevel);

            }
        });




        //<editor-fold defaultstate="collapsed" desc="== combo  action ==">
        selectDeviceCombo.valueProperty().addListener(new ChangeListener<UsbDevice>() {

            @Override
            public void changed(ObservableValue ov, UsbDevice oldDevice, UsbDevice newDevice) {
            String result = adbService.setDevice(false, newDevice.getSerial());
            terminalOutput.appendText(result);
                terminalOutput.setStyle(0, result.length()-1, String.format("-fx-fill:%s;", LogLineColor.WARN.getColorAsString() ));
            }
        });
        //</editor-fold>
    }

    public void shutdown(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void runLogCat(ActionEvent actionEvent) {
        String filterText = filterField.getText().trim();
        if(!filterText.isEmpty()|| filterText.length() > 0) {
            adbService.setFilter(filterText);
        }
        adbService.runLogCatScheduledService(terminalOutput);
    }


    public void setFilter(ActionEvent actionEvent) {
        String filterText =filterField.getText().trim();
        if(!filterText.isEmpty()) {
            terminalOutput.clear();
            adbService.setFilter(filterField.getText());
            filterDisplay.setText(String.format(DEFAULT_FILTER_TEST, filterText));
        }

    }

    @Override
    public void set2DBounds(Rectangle2D bounds) {
        double perfWidth = bounds.getWidth()*0.9;
        double perfHeight = bounds.getHeight()*0.75;
        mainContainer.setPrefSize( perfWidth, perfHeight);
        terminalOutputScrollPane.setPrefSize(perfWidth, perfHeight );


    }

    public void stopLogCat(ActionEvent actionEvent) {
        adbService.stopLogStream();
    }

    public void restartLogCat(ActionEvent actionEvent) {
        adbService.startLogStream();
    }

    public void clearLogCat(ActionEvent actionEvent) {

        terminalOutput.replaceText("");

    }

    public void clearFilter(ActionEvent actionEvent) {
        adbService.setFilter(ADB_COMMANDS.SET_NO_FILTER.name());
        filterDisplay.setText(String.format(DEFAULT_FILTER_TEST, filter.equals(ADB_COMMANDS.SET_NO_FILTER.name())));
    }
}