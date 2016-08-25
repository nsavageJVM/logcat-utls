package com.fishbeans;

import com.fishbeans.app.AppContextExt;
import com.fishbeans.app.ui.controllers.view.BasePanelView;
import com.fishbeans.service.ADBService;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

/**
 * Created by ubu on 8/20/16.
 */
@Lazy
@SpringBootApplication
public class EntryPoint extends AppContextExt {

    /**
     * Note that this is configured in application.properties
     */
    @Value("${app.stage.header:ADB Utils}")//
    private String stageHeader;


    @Autowired
    BasePanelView entryView;


    @Override
    public void start(Stage stage) throws Exception {

//        String result = adbService.setDevice();
//        System.out.println(result);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Scene  scene = new Scene(entryView.getRootNode(), bounds.getWidth() - bounds.getWidth() / 10, bounds.getHeight() - bounds.getHeight() / 3.5);
        stage.setTitle(stageHeader);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }


    public static void main(String[] args) {
        launchApp(EntryPoint.class, args);
    }


}
