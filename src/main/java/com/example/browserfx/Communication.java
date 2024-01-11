package com.example.browserfx;

import com.example.browserfx.ui.WebTab;
import javafx.concurrent.Worker;

public interface Communication {
    void updateSearchField(String str);
    void updateTabTitle(String str);
    void closeTabListener(WebTab obj);
    void loadingHandler(Worker.State state);
    void addBookmark(String title, String link);
}
