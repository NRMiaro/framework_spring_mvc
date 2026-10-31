package framework.core;

public class ViewNameHandler {
    public static String prefix, suffix;

    public static void setPrefix(String prefix) {
        ViewNameHandler.prefix = prefix;
    }

    public static void setSuffix(String suffix) {
        ViewNameHandler.suffix = suffix;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String getSuffix() {
        return suffix;
    }

    public static String resolve(String pageName) {
        return prefix + pageName + suffix;
    }
}
