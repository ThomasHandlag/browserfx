package com.example.browserfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse the pure string css
 */
public class CssParser {
    public static String format(String css) {
        String temp = "";
        return temp;
    }

    /**
     *
     * @param cssString String
     * @return colored String
     */
    public static String colorizeCSS(String cssString) {
        // Define a regular expression pattern to match hex color values
        String hexColorPattern = "#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})\\b";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(hexColorPattern);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(cssString);

        // Use StringBuffer for efficient string concatenation
        StringBuilder coloredCSS = new StringBuilder();

        // Find and replace hex colors with their actual color
        while (matcher.find()) {
            String hexColor = matcher.group(0);
            System.out.println(hexColor);
            Map<String, Integer> color = hexToColor(hexColor);
            String rgbColor = "rgb(" + color.get("red") + ", " + color.get("green") + ", " + color.get("blue") + ")";
            matcher.appendReplacement(coloredCSS, rgbColor);
        }
        matcher.appendTail(coloredCSS);
        return coloredCSS.toString();
    }

    /**
     *
     * @param hex String hex color value
     * @return color map rgb
     */
    public static Map<String, Integer> hexToColor(String hex) {
        Map<String, Integer> mapColor = new HashMap<>();
        if (hex.length() > 7) {
            mapColor.put("red",
                    Integer.valueOf(hex.substring(1, 3), 16)
            );
            mapColor.put("green",
                    Integer.valueOf(hex.substring(3, 5), 16)
            );
            mapColor.put("blue",
                    Integer.valueOf(hex.substring(5, 7), 16)
            );
        }
        return mapColor;
    }
}
