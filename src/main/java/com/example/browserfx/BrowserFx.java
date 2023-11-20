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
        public void switchToInspect(String obj) {
            mode = Constant.DEV_MODE;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/ui/inspect-ui.fxml"));
            try {
                Scene scene1 = new Scene(fxmlLoader.load(), 800, 600);
                scene1.getStylesheets().add(Objects.requireNonNull(
                                getClass().getResource("css/main.css"))
                        .toExternalForm());
                stage.setScene(scene1);
                InspectController controller1 = fxmlLoader.getController();
                controller1.editor.setText(obj);
                controller1.scenePasser = scenePasser;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         *
         *
         */
        private final ScenePasser scenePasser = () -> reloadMainScene();

        @Override
        public Boolean getTheme() {
            return theme;
        }
    };

    @Override
    public void start(Stage stage) throws IOException {
        //load main scene
        this.stage = stage;
        loadMainScene();
        stage.setTitle("BrowserFx");
        stage.show();
    }

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

    protected void keyHandler(KeyEvent event) {
        if (event.getCode() == KeyCode.F12) {
            {
                if (mode == Constant.NORMAL_MODE) {
                    controller.inspectWeb();
                }
                else if (mode == Constant.DEV_MODE) {
                        reloadMainScene();
                }
            }
        } else if (event.getCode() == KeyCode.F5) {
            if (controller.currentTabNotNull())
                controller.activeTab.reload();
        }
    }

    private void reloadMainScene() {
        stage.setScene(scene);
        mode = Constant.NORMAL_MODE;
    }


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