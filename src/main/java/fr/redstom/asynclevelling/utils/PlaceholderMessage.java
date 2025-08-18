package fr.redstom.asynclevelling.utils;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PlaceholderMessage {

    private final String message;
    private final Map<String, String> placeholders = new HashMap<>();

    public PlaceholderMessage with(String placeholder, String value) {
        placeholders.put(placeholder, value);
        return this;
    }

    public String replace() {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return result;
    }
}
