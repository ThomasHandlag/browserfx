package com.example.browserfx.constants;

public final class Scripts {
    public static final String VIEW_HTML = "document.documentElement.outerHTML";
    public static final String VIEW_CSS = "var style = document.createElement('style');"
            + "document.head.appendChild(style); "
            + "style.innerHTML = Array.from(document.styleSheets).map(sheet => Array.from(sheet.cssRules).map(rule => rule.cssText).join('\\n')).join('\\n');";
}
