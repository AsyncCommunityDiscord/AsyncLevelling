package fr.redstom.asynclevelling.utils;

public class NumberUtils {
    static String formatNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format("%.1fB", number / 1_000_000_000d);
        } else if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000d);
        } else if (number >= 1000) {
            return String.format("%.1fk", number / 1_000d);
        } else {
            return String.valueOf(number);
        }
    }
}
