package com.example.browserfx.controllers;

import com.example.browserfx.BrowserFx;
import com.example.browserfx.Communication;
import com.example.browserfx.Teller;
import com.example.browserfx.constants.Scripts;
import com.example.browserfx.ui.WebTab;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class BrowserController implements Initializable {
    public int tabCount = 1;
    public WebTab activeTab;
    public Button backBtn;
    public Button forwardBtn;
    public Button reloadBtn;
    public TextField searchTextField;
    public MenuButton menuBtn;
    public ToggleButton themeBtn;
    public TabPane tabPane;
    public Button clearBtn;
    public Button addTabBtn;

    private static final Map<String, WebTab> tabHistory = new HashMap<>();
    public Button homeBtn;
    private final String newTabTitle = "New tab";
    public VBox bookmarkBar;
    private Teller teller;
    private Worker.State currentWebTabState;
    public static HashMap<String, String> bookmarkMap = null;
    public Stage stage = null;

    Communication communication = new Communication() {
        @Override
        public void updateSearchField(String str) {
            searchTextField.setText(str);
        }

        @Override
        public void updateTabTitle(String str) {
            activeTab.setText(str);
        }

        @Override
        public void closeTabListener(WebTab obj) {
            MenuItem item = new MenuItem(obj.getText());
            item.setId(tabHistory.size() + "");
            tabHistory.put(tabHistory.size() + "", obj);
            item.setOnAction(event -> {
                WebTab tab = tabHistory.get(item.getId());
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
                menuBtn.getItems().remove(item);
            });
            menuBtn.getItems().add(item);
        }

        @Override
        public void loadingHandler(Worker.State state) {
            currentWebTabState = state;
            FontIcon icon = new FontIcon(
                    Objects.requireNonNull(state) == Worker.State.RUNNING ? "far-circle" : "fas-redo"
            );
            icon.setIconColor(teller.getTheme() ? Color.WHITE : Color.BLACK);
            reloadBtn.graphicProperty().setValue(icon);
        }

        @Override
        public void addBookmark(String title, String link) {
            if (bookmarkMap != null)
                bookmarkMap.put(title, link);
            Button newBmBtn = new Button(title);
            newBmBtn.setOnAction(e -> activeTab.loadURl(link));
            bookmarkBar.getChildren().add(newBmBtn);
            try {
                URI uri = new URI(
                        Objects.requireNonNull(
                                Objects.requireNonNull(
                                        BrowserFx.class.getResource("config/bookmarklist.brs")).toString()
                        )
                );
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(uri)));
                bookmarkMap.forEach((t, l) -> {
                    String line = "\n" + t + "<:>" + l;
                    try {
                        bufferedWriter.write(line);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                bufferedWriter.close();
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backBtn.setTooltip(new Tooltip("Back"));
        forwardBtn.setTooltip(new Tooltip("Forward"));
        reloadBtn.setTooltip(new Tooltip("Reload"));
        homeBtn.setTooltip(new Tooltip("Home"));
        clearBtn.setTooltip(new Tooltip("Clear history"));
        addTabBtn.setTooltip(new Tooltip("New tab"));
        themeBtn.setTooltip(new Tooltip("Toggle theme"));
        menuBtn.setTooltip(new Tooltip("History"));
        WebTab webTab = new WebTab(newTabTitle, communication);
        webTab.setId("" + tabCount);
        tabPane.getTabs().add(webTab);
        activeTab = webTab;
        tabPane.getSelectionModel().selectedItemProperty().addListener((ob, old, newtab) -> {
            tabCount = tabPane.getTabs().size();
            if (newtab != null) {
                activeTab = (WebTab) newtab;
                if (!"New tab".equals(activeTab.getText())) {
                    searchTextField.setText(activeTab.getText());
                } else searchTextField.setText("");
            } else {
                stage.close();
            }
        });
        try {
            bookmarkMap = new HashMap<>();
            String settingPath = "config/bookmarklist.brs";
            URI uri = new URI(Objects.requireNonNull(
                    BrowserFx.class.getResource(settingPath)).toString()
            );
            File file = new File(uri);
            BufferedReader readLine = new BufferedReader(new FileReader(file));
            Stream<String> lines = readLine.lines();
            lines.forEach((line) -> {
                if (line.contains("<:>")) {
                    String title = line.substring(0, line.indexOf("<:>"));
                    String link = line.substring(line.indexOf("<:>")+3);
                    bookmarkMap.put(title, link);
                }
            });
            readLine.close();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        if (bookmarkMap != null) {
            bookmarkMap.forEach((title, link) -> {
                Button bookmarkBtn = new Button(title);
                bookmarkBtn.getStyleClass().add("bookmark-btn");
                bookmarkBtn.setTooltip(new Tooltip(title));
                bookmarkBtn.setOnAction(event -> activeTab.loadURl(link));
                bookmarkBar.getChildren().add(bookmarkBtn);
            });
        } else {
            bookmarkBar.prefWidth(0);
        }
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
    protected void handleReload() {
        if (activeTab != null) {
            activeTab.reload();
        }
    }

    @FXML
    protected void clearHistory() {
        menuBtn.getItems().clear();
    }

    @FXML
    protected void handleSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            activeTab.loadURl(searchTextField.getText());
    }

    @FXML
    protected void handleTheme() {
        boolean theme = themeBtn.isSelected();
        teller.themeHandler(theme);
        FontIcon icon = new FontIcon(theme ? "fas-sun" : "fas-moon");
        icon.setIconColor(theme ? Color.BLACK : Color.WHITE);
        themeBtn.setGraphic(icon);
        try {
            URI uri = new URI(String.valueOf(BrowserFx.class.getResource("config/theme.brs")));
            File file = new File(uri);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String themeValue = theme ? "theme dark" : "theme light";
            bufferedWriter.write(themeValue);
            bufferedWriter.close();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inspect current website and navigate to the inspect view with html and css data
     */
    public void inspectWeb() {
        if (currentWebTabState == Worker.State.SUCCEEDED) {
            String htmlContent = (String) activeTab.webEngine.executeScript(Scripts.VIEW_HTML);
            String styles = "";
            //  Some website do not allow to read their css style
            // make this alert to avoid JS exception occur @styles
            try {
                styles = (String) activeTab.webEngine.executeScript(Scripts.VIEW_CSS);
                styles = styles.replaceAll(";", ";\n\t");
                styles = styles.replaceAll("\t}", "}");
                styles = styles.replaceAll("\\{", "{\n\t");
                teller.switchToInspect(htmlContent, styles);
            } catch (Exception e) {
                Alert dialog = new Alert(Alert.AlertType.WARNING);
                dialog.setTitle("Error");
                dialog.setContentText("This website is restrict for viewing CSS!");
                dialog.show();
            }
        }
    }

    @FXML
    public void handleHomepage() {
        if (activeTab != null)
            activeTab.home();
    }


    @FXML
    public void handleAddTab() {
        WebTab webTab = new WebTab(newTabTitle, communication);
        webTab.setId("" + (tabCount + 1));
        tabPane.getTabs().add(webTab);
        tabPane.getSelectionModel().select(webTab);
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
            FontIcon icon = new FontIcon("fas-sun");
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

    public void textFieldClick() {
        searchTextField.selectAll();
    }
}