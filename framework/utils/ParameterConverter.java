package framework.utils;

public class ParameterConverter {

    public static Object convert(String value, Class<?> targetType) {

        if (value == null) {
            return null;
        }

        if (targetType == String.class) {
            return value;
        }

        if (targetType == int.class) {
            return Integer.parseInt(value);
        }

        if (targetType == double.class) {
            return Double.parseDouble(value);
        }

        if (targetType == float.class) {
            return Float.parseFloat(value);
        }

        if (targetType == long.class) {
            return Long.parseLong(value);
        }

        if (targetType == short.class) {
            return Short.parseShort(value);
        }

        if (targetType == byte.class) {
            return Byte.parseByte(value);
        }

        if (targetType == boolean.class) {
            if (!value.equalsIgnoreCase("true")
                    && !value.equalsIgnoreCase("false")) {
                throw new IllegalArgumentException(
                    "Valeur booléenne invalide : " + value
                );
            }

            return Boolean.parseBoolean(value);
        }

        if (targetType == char.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException(
                    "Un caractère est attendu, valeur reçue : " + value
                );
            }

            return value.charAt(0);
        }

        throw new IllegalArgumentException(
            "Type non supporté : " + targetType.getName()
        );
    }
}
