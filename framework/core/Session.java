package framework.core;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> attributes = new HashMap<>();

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}