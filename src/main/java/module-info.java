module com.example.browserfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.web;
    opens com.example.browserfx to javafx.fxml;
    exports com.example.browserfx;
    exports com.example.browserfx.controllers;
    opens com.example.browserfx.controllers to javafx.fxml;
    exports com.example.browserfx.ui;
    opens com.example.browserfx.ui to javafx.fxml;
}