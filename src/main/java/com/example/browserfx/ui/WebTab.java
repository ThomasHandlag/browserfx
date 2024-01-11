package com.example.browserfx.ui;

import com.example.browserfx.BrowserFx;
import com.example.browserfx.Communication;
import javafx.concurrent.Worker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class WebTab extends Tab {
    public WebView webView;
    public WebEngine webEngine;
    public WebHistory history;
    public static Boolean theme = false;
    private static final String WWW_GOOGLE_COM = "https://www.google.com";
    private static final String INDEX_PATH = Objects
            .requireNonNull(
                    BrowserFx.class
                            .getResource("web/index.html"))
            .toExternalForm();
    private final Communication communication;

    public WebTab(String title, Communication inf) {
        super(title);
        initialize();
        this.communication = inf;
    }

    private String defaultTab = "";

    /**
     * current url of the tab, this is used to reload
     */
    public String currentUrl;

    /**
     * Init all needed field
     */
    private void initialize() {
        MenuItem bookmarkItem = new MenuItem("Bookmark this page");
        bookmarkItem.setOnAction(e -> {
                    TextInputDialog dialog = new TextInputDialog(getText());
                    dialog.setTitle("Add bookmark -> " + currentUrl);
                    dialog.setHeaderText("Enter your text:");
                    dialog.showAndWait().ifPresent(input -> communication.addBookmark(input, currentUrl));
                }
        );
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(bookmarkItem);
        setContextMenu(contextMenu);
        webView = new WebView();
        webEngine = webView.getEngine();
        defaultTab = Objects
                .requireNonNull(
                        BrowserFx.class
                                .getResource("web/index.html"))
                .toExternalForm();
        // Add listener to worker for catching event
        webEngine.getLoadWorker().stateProperty().addListener((observable, old, newVal) -> {
            if (newVal == Worker.State.SUCCEEDED) {
                String location = webEngine.getLocation();
                currentUrl = location.substring(0, 4).contains("file") ? "" : location;
                communication.updateSearchField(currentUrl);
                communication.loadingHandler(newVal);
                if (webEngine.getTitle() != null)
                    if (webEngine.getTitle().contains("Google"))
                        setText("New tab");
                    else setText(webEngine.getTitle());
                else setText("New tab");
            } else {
                if (communication != null)
                    communication.loadingHandler(newVal);
            }
        });
        history = webEngine.getHistory();
        webEngine.load(defaultTab);
        setContent(webView);
        setOnCloseRequest((event) -> communication.closeTabListener(this));
    }

    public void back() {
        int currentIndex = history.getCurrentIndex();
        if (currentIndex > 0) {
            history.go(-1);
        }
    }

    public void forward() {
        int currentIndex = history.getCurrentIndex();
        if (currentIndex < history.getMaxSize() - 1) {
            history.go(1);
        }
    }

    public void reload() {
        webEngine.reload();
    }

    public void home() {
        webEngine.load(INDEX_PATH);
    }

    public void loadURl(String url) {
        if (isValidHttpUri(url)) {
            webEngine.load(url);
        } else if (Objects.equals(url, "")) {
            webEngine.load(defaultTab);
        } else {
            webEngine.load(WWW_GOOGLE_COM + "/search?q=" + url + "&oq=" + url + "&sourceid=chrome&ie=UTF-8");
        }
    }

    public boolean isValidHttpUri(String uriString) {
        try {
            URI uri = new URI(uriString);
            String scheme = uri.getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
