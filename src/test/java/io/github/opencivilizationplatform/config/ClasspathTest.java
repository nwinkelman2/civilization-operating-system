package io.github.opencivilizationplatform.config;

import org.junit.jupiter.api.Test;

public class ClasspathTest {
    @Test
    public void testClasspath() {
        try {
            Class<?> clazz3 = Class.forName("tools.jackson.databind.ObjectMapper");
            System.out.println("Jackson 3 ObjectMapper class found: " + clazz3.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("Jackson 3 ObjectMapper class NOT found: " + e.getMessage());
        }
        try {
            Class<?> clazz2 = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            System.out.println("Jackson 2 ObjectMapper class found: " + clazz2.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("Jackson 2 ObjectMapper class NOT found: " + e.getMessage());
        }
    }
}
