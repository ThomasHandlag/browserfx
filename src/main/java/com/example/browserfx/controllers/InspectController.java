package com.example.browserfx.controllers;

import com.example.browserfx.ScenePasser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class InspectController implements Initializable {

    public TextArea editor;
    public ScenePasser scenePasser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editor.setWrapText(true);
        editor.setFont(javafx.scene.text.Font.font("Monospaced", 15)); // Use a monospaced font
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
        });
        editor.setText(formatHTML(editor.getText()));
    }
    private String formatHTML(String htmlCode) {
        // Implement your logic to format the HTML code (e.g., add indentation)
        // You can use regular expressions or other string manipulation techniques.
        // Here's a simple example:

        // Replace all occurrences of '>' with '>\n'
        htmlCode = htmlCode.replaceAll(">", ">\n");
        htmlCode = htmlCode.replaceAll(";", ";\n");
        // Add indentation to each line
        String[] lines = htmlCode.split("\n");
        StringBuilder formattedCode = new StringBuilder();
        int indentationLevel = 0;

        for (String line : lines) {
            if (line.startsWith("</")) {
                indentationLevel--;
            }

            for (int i = 0; i < indentationLevel; i++) {
                formattedCode.append("    ");
            }

            formattedCode.append(line).append("\n");

            if (line.endsWith(">") && !line.startsWith("</")) {
                indentationLevel++;
            }
        }

        return formattedCode.toString();
    }
    @FXML
    public void backToMain() {
        scenePasser.loader();
    }
}
