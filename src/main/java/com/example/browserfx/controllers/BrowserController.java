package com.example.browserfx.controllers;

import com.example.browserfx.Communication;
import com.example.browserfx.Teller;
import com.example.browserfx.ui.WebTab;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

public class BrowserController implements Initializable {
    public int tabCount = 1;
    public WebTab activeTab;
    public Button backBtn;
    public Button forwardBtn;
    public Button reloadBtn;
    public TextField seachTextfield;
    public MenuButton menuBtn;
    public ToggleButton themeBtn;
    public TabPane tabPane;
    public Button clearBtn;
    public Button addTabBtn;

    private static final Map<String, WebTab> tabHistory = new HashMap<>();
    public Button homeBtn;
    private final String newTabTitle = "New tab";
    private Teller teller;


    Communication inf = new Communication() {
        @Override
        public void updateSearchField(String str) {
            seachTextfield.setText(str);
        }

        @Override
        public void updateTabTitle(String str) {
            activeTab.setText(str);
        }

        @Override
        public void closeTabListener(WebTab obj) {
            MenuItem item = new MenuItem(obj.currentUrl);
            item.setId(tabHistory.size() + "");
            tabHistory.put(tabHistory.size() + "", obj);
            item.setOnAction(event -> {
                tabPane.getTabs().add(tabHistory.get(item.getId()));
                menuBtn.getItems().remove(item);
            });
            menuBtn.getItems().add(item);
        }

        @Override
        public void loadingHandler(Worker.State state) {
            FontIcon icon = new FontIcon(
                    Objects.requireNonNull(state) == Worker.State.RUNNING ? "far-circle" : "fas-redo"
            );
            icon.setIconColor(teller.getTheme() ? Color.WHITE : Color.BLACK);
            reloadBtn.graphicProperty().setValue(icon);
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WebTab webTab = new WebTab(newTabTitle, inf);
        webTab.setId("" + tabCount);
        tabPane.getTabs().add(webTab);
        activeTab = webTab;
        tabPane.getSelectionModel().selectedItemProperty().addListener((ob, old, newtab) -> {
            tabCount = tabPane.getTabs().size();
            if (newtab != null) {
                activeTab = (WebTab) newtab;
            }
        });
        themeBtn.setOnAction(event -> {
            boolean theme = themeBtn.isSelected();
            teller.themeHandler(theme);
            FontIcon icon = new FontIcon(theme ? "fas-sun" : "fas-moon");
            icon.setIconColor(theme ? Color.BLACK : Color.WHITE);
            themeBtn.setGraphic(icon);
        });
    }

    @FXML
    protected void handleBack() {
        if (activeTab != null)
            activeTab.back();
    }

    @FXML
    protected void handleForward() {
        if (activeTab != null)
            activeTab.forward();
    }

    @FXML
    protected void handleReload(MouseEvent event) {
        if (activeTab != null) {
            activeTab.reload();
        }
        System.out.println(event.isBackButtonDown());
    }

    @FXML
    protected void clearHistory() {
        menuBtn.getItems().clear();
    }

    @FXML
    protected void handleSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            activeTab.loadURl(seachTextfield.getText());
    }

    @FXML
    protected void handleTheme() {

    }

    public void inspectWeb() {
        String htmlContent = (String) activeTab.webEngine.executeScript("document.documentElement.outerHTML");
        teller.switchToInspect(htmlContent);
    }

    @FXML
    public void handleHomepage() {
        if (activeTab != null)
            activeTab.home();
    }


    @FXML
    public void handleAddTab() {
        WebTab webTab = new WebTab(newTabTitle, inf);
        webTab.setId("" + (tabCount + 1));
        tabPane.getTabs().add(webTab);
    }

    public Boolean currentTabNotNull() {
        return activeTab != null;
    }

    public void setTeller(Teller teller) {
        this.teller = teller;
    }

    public void loadStyle() {
        if (teller.getTheme()) {
            backBtn.setTextFill(Color.WHITE);
            forwardBtn.setTextFill(Color.WHITE);
            reloadBtn.setTextFill(Color.WHITE);
            clearBtn.setTextFill(Color.WHITE);
            addTabBtn.setTextFill(Color.WHITE);
            homeBtn.setTextFill(Color.WHITE);
            ((FontIcon) backBtn.getGraphic()).setIconColor(Color.WHITE);
            ((FontIcon) forwardBtn.getGraphic()).setIconColor(Color.WHITE);
            ((FontIcon) reloadBtn.getGraphic()).setIconColor(Color.WHITE);
            ((FontIcon) clearBtn.getGraphic()).setIconColor(Color.WHITE);
            ((FontIcon) addTabBtn.getGraphic()).setIconColor(Color.WHITE);
            ((FontIcon) homeBtn.getGraphic()).setIconColor(Color.WHITE);
            FontIcon icon = new FontIcon("fas-sun" );
            icon.setIconColor(Color.BLACK);
            themeBtn.setGraphic(icon);
            WebTab.theme = true;
        } else {
            backBtn.setTextFill(Color.BLACK);
            forwardBtn.setTextFill(Color.BLACK);
            reloadBtn.setTextFill(Color.BLACK);
            clearBtn.setTextFill(Color.BLACK);
            addTabBtn.setTextFill(Color.BLACK);
            homeBtn.setTextFill(Color.BLACK);
            ((FontIcon) backBtn.getGraphic()).setIconColor(Color.BLACK);
            ((FontIcon) forwardBtn.getGraphic()).setIconColor(Color.BLACK);
            ((FontIcon) reloadBtn.getGraphic()).setIconColor(Color.BLACK);
            ((FontIcon) clearBtn.getGraphic()).setIconColor(Color.BLACK);
            ((FontIcon) addTabBtn.getGraphic()).setIconColor(Color.BLACK);
            ((FontIcon) homeBtn.getGraphic()).setIconColor(Color.BLACK);
            FontIcon icon = new FontIcon("fas-moon");
            icon.setIconColor(Color.WHITE);
            themeBtn.setGraphic(icon);
            WebTab.theme = false;
        }
    }
}