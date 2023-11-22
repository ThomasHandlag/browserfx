package com.example.browserfx.controllers;

import com.example.browserfx.ScenePasser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        htmlViewer.setWrapText(true);
        htmlViewer.setFont(javafx.scene.text.Font.font("Monospaced", 18)); // Use a monospaced font
        htmlViewer.textProperty().addListener((observable, oldValue, newValue) -> {
        });
        htmlViewer.setText(formatHTML(htmlViewer.getText()));
    }

    private String formatHTML(String htmlCode) {

        htmlCode = htmlCode.replaceAll(">", ">\n");
        htmlCode = htmlCode.replaceAll(";", ";\n");
        String[] lines = htmlCode.split("\n");
        StringBuilder formattedCode = new StringBuilder();
        int indentationLevel = 0;

        for (String line : lines) {
            if (line.startsWith("</")) {
                indentationLevel--;
            }
            formattedCode.append("    ".repeat(Math.max(0, indentationLevel)));
            formattedCode.append(line).append("\n");

            if (line.endsWith(">") && !line.startsWith("</")) {
                indentationLevel++;
            }
        }

        return formattedCode.toString();
    }

    public void onDragSplitPane(MouseDragEvent event) {
        double totalWidth = splitPane.getWidth();
        double mouseX = event.getX();

        // Calculate the new divider position based on mouse position
        double newDividerPosition = mouseX / totalWidth;

        // Set the new divider position
        splitPane.setDividerPositions(newDividerPosition);
    }

    @FXML
    public void backToMain() {
        scenePasser.loader();
    }
}
