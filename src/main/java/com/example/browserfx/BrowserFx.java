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

import java.io.IOException;
import java.util.Objects;

public class BrowserFx extends Application {

    private Boolean theme = false;

    private Constant mode = Constant.NORMAL_MODE;
    private BrowserController controller;
    private Scene scene;
    private InspectController inspectController;
    private Stage stage;

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
        Scene scene1 = new Scene(fxmlLoader.load(), 800, 600);
        scene1.getStylesheets().add(Objects.requireNonNull(
                        getClass().getResource("css/main.css"))
                .toExternalForm());
        scene1.setOnKeyPressed(this::keyHandler);
        stage.setScene(scene1);
        inspectController = fxmlLoader.getController();
        inspectController.scenePasser = scenePasser;
        inspectController.htmlViewer.setText(html);
        inspectController.cssViewer.setText(CssParser.colorizeCSS(css));
    }

    @Override
    public void start(Stage stage) throws IOException {
        //load main scene
        this.stage = stage;
        loadMainScene();
        stage.setTitle("BrowserFx");
        stage.show();
    }

    /**
     * Load main scene with Google search bar
     * @throws IOException avoid fxml file is missing
     */
    public void loadMainScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BrowserFx.class.getResource("fxml/main-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        setStyle();
        controller = fxmlLoader.getController();
        controller.setTeller(this.teller);
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
                controller.seachTextfield.requestFocus();
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
        this.theme = theme;
    }

    public static void main(String[] args) {
        launch();
    }
}