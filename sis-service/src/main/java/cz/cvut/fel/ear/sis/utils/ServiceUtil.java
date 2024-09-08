package cz.cvut.fel.ear.sis.utils;

public class ServiceUtil {
    public static boolean doesNotConformRegex(String input, String regexPattern) {
        return input == null || !input.matches(regexPattern);
    }

}

