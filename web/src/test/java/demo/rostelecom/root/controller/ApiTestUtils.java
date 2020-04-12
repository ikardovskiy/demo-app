package demo.rostelecom.root.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "UnnecessaryLocalVariable"})
public class ApiTestUtils {
    public static String loadResource(Class clazz, String resource) throws IOException {

        try(BufferedReader inputStreamReader =
                    new BufferedReader(
                            new InputStreamReader(clazz.getResourceAsStream(resource), StandardCharsets.UTF_8))) {
            String script = inputStreamReader.lines().collect(Collectors.joining());
            return script;
        }
    }
}
