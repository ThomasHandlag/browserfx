package com.example.browserfx.controllers;

import com.example.browserfx.ScenePasser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class InspectController implements Initializable {

    public TextArea htmlViewer;
    public ScenePasser scenePasser;
    public Button backBtn;
    public Button upBtn;
    public Button downBtn;
    public Label counter;
    public Label url;
    public SplitPane splitPane;
    public TextArea cssViewer;
    public TextField searchField;
    public boolean theme;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        htmlViewer.setWrapText(true);
        htmlViewer.setFont(javafx.scene.text.Font.font("Monospaced", 18)); // Use a monospaced font
        htmlViewer.textProperty().addListener((observable, oldValue, newValue) -> {
        });
        htmlViewer.setText(formatHTML(htmlViewer.getText()));
        cssViewer.setWrapText(true);
        ((FontIcon)backBtn.getGraphic()).setIconColor(theme ? Color.DARKMAGENTA : Color.WHITE);
//        htmlViewer.setOnContextMenuRequested(event -> {
//            String selected = htmlViewer.getSelectedText();
//        });
    }

    private String formatHTML(String htmlCode) {
        String rtString = htmlCode.replaceAll(">", ">\n");
        rtString = rtString.replaceAll("<", "\t<");
        return rtString;
    }

    public void onDragSplitPane(MouseEvent event) {
        double totalWidth = splitPane.getWidth();
        double mouseX = event.getX();
        double newDividerPosition = mouseX / totalWidth;
        splitPane.setDividerPositions(newDividerPosition);
    }

    @FXML
    public void backToMain() {
        scenePasser.loader();
    }
}
