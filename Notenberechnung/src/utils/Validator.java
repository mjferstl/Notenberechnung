package utils;

public class Validator {

    public static boolean isValidString(String string) {
        return (string != null && !string.trim().isEmpty());
    }
}
