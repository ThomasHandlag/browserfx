package com.example.browserfx;

/**
 * A callback interface
 */
public interface Teller {
    void themeHandler(Boolean theme);
    void switchToInspect(String html, String css);
    Boolean getTheme();
}
