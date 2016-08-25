package com.fishbeans.app.ui.controllers.view.components;

import com.fishbeans.util.ResourceUtils;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * works with BasePanelView
 */
@Component
@NoArgsConstructor
public class TerminalView extends Tab {

    private static final String CSS_DIR = "css/";

    private InlineCssTextArea output;
    private VirtualizedScrollPane<InlineCssTextArea > vsPane;

    @PostConstruct
    void init() {
        this.output = new InlineCssTextArea();
         vsPane = new VirtualizedScrollPane<>(output);

        this.setText("Log Data#");
        VBox box = new VBox();
        this.output.setFocusTraversable(false);
        this.output.setEditable(false);
        box.getChildren().add(vsPane);
        this.setContent(box);
        VBox.setVgrow(output, Priority.ALWAYS);
        box.getStylesheets().addAll( ResourceUtils.getResource(CSS_DIR +"terminal.css" ).toExternalForm());
    }



    public InlineCssTextArea getOutput() {
        return output;
    }

    public VirtualizedScrollPane<InlineCssTextArea > getScrollPane() {
        return vsPane;
    }




}
