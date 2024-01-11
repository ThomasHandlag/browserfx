package com.example.browserfx;

import com.example.browserfx.constants.Constant;
import com.example.browserfx.controllers.BrowserController;
import com.example.browserfx.controllers.InspectController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

public class BrowserFx extends Application {

    private static Boolean theme = false;

    private Constant mode = Constant.NORMAL_MODE;
    private BrowserController controller;
    private Scene scene;
    private InspectController inspectController;
    private Stage stage;
    private double width = 1000, height = 600, positionX = 10, positionY = 10;
    /**
     *
     */
    private final Teller teller = new Teller() {
        @Override
        public void themeHandler(Boolean theme) {
            setTheme(theme);
            controller.loadStyle();
            setStyle();
        }

        @Override
        public void switchToInspect(String html, String css) {
            mode = Constant.DEV_MODE;
            try {
                devScene(html, css);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Boolean getTheme() {
            return theme;
        }
    };
    private final ScenePasser scenePasser = this::reloadMainScene;

    /**
     * Load dev scene and pass it to stage
     * {@code @REQUIRE html: HTML string, css: CSS string}
     */
    public void devScene(String html, String css) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/ui/inspect-ui.fxml"));
        Scene scene1 = new Scene(fxmlLoader.load(), width, height);
        scene1.getStylesheets().add(Objects.requireNonNull(
                        getClass().getResource(theme ? "css/main-dark.css" : "css/main.css"))
                .toExternalForm());
        scene1.setOnKeyPressed(this::keyHandler);
        stage.setScene(scene1);
        inspectController = fxmlLoader.getController();
        inspectController.scenePasser = scenePasser;
        inspectController.htmlViewer.setText(html);
        inspectController.theme = theme;
        inspectController.cssViewer.setText(CssParser.colorizeCSS(css));
    }

    @Override
    public void start(Stage stage) throws IOException {
        //load main scene
        this.stage = stage;
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            width = newValue.doubleValue();
        });

        // Add listener for window horizontal position changes
        stage.xProperty().addListener((observable, oldValue, newValue) -> {
            positionX = newValue.doubleValue();
        });

        // Add listener for window vertical position changes
        stage.yProperty().addListener((observable, oldValue, newValue) -> {
            positionY = newValue.doubleValue();
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            height = newValue.doubleValue();
        });
        getTheme();
        loadMainScene();
        stage.setTitle("BrowserFx");
        stage.show();
    }

    private void getTheme() {
        try {
            String settingPath = "config/theme.brs";
            URI uri = new URI(Objects.requireNonNull(
                    BrowserFx.class.getResource(settingPath)).toString()
            );
            File file = new File(uri);
            BufferedReader readLine = new BufferedReader(new FileReader(file));
            Stream<String> lines = readLine.lines();
            lines.forEach((line) -> {
                if (line.contains("theme")) {
                    String themeValue = line.substring(line.indexOf(' ')+1);
                    theme = themeValue.equals("dark");
                }
            });
            readLine.close();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load main scene with Google search bar
     *
     * @throws IOException avoid fxml file is missing
     */
    public void loadMainScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BrowserFx.class.getResource("fxml/main-view.fxml"));
        scene = new Scene(fxmlLoader.load(), width, height);
        stage.setScene(scene);
        setStyle();
        controller = fxmlLoader.getController();
        controller.setTeller(this.teller);
        controller.stage = stage;
        controller.loadStyle();
        scene.setOnKeyPressed(this::keyHandler);
    }

    /**
     * @param event handle keyboard event for scenes
     */
    protected void keyHandler(KeyEvent event) {
        if (event.getCode() == KeyCode.F12) {
            {
                if (mode == Constant.NORMAL_MODE) {
                    controller.inspectWeb();
                } else if (mode == Constant.DEV_MODE) {
                    reloadMainScene();
                }
            }
        } else if (event.getCode() == KeyCode.F5) {
            if (controller.currentTabNotNull())
                controller.activeTab.reload();
        } else if (event.getCode() == KeyCode.F && event.isControlDown()) {
            if (stage.getScene() == scene) {
                controller.searchTextField.requestFocus();
            } else {
                inspectController.searchField.requestFocus();
            }
        }
    }

    /**
     * Set stage with main scene
     */
    private void reloadMainScene() {
        stage.setScene(scene);
        mode = Constant.NORMAL_MODE;
    }

    /**
     * Set theme style depend on application theme value
     */
    public void setStyle() {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(
                theme ? Objects.requireNonNull(getClass().getResource("css/main-dark.css")).toExternalForm()
                        : Objects.requireNonNull(getClass().getResource("css/main.css")).toExternalForm());
    }

    public void setTheme(Boolean theme) {
        BrowserFx.theme = theme;
    }

    public static void main(String[] args) {
        launch();
    }
}