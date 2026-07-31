package com.bhavana.aidoc.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JavaCodeParserTest {

    private final JavaCodeParser parser = new JavaCodeParser();

    @Test
    void testParseJavaClassWithAnnotationsAndMethods() {
        String code = """
                package com.example.demo;

                import org.springframework.web.bind.annotation.*;

                @RestController
                @RequestMapping("/api/users")
                public class UserController {

                    private String service;

                    @GetMapping("/{id}")
                    public String getUser(@PathVariable String id) {
                        return "User " + id;
                    }
                }
                """;

        JavaCodeParser.ClassInfo info = parser.parseJavaCode(code);

        assertEquals("UserController", info.getName());
        assertEquals("com.example.demo", info.getPackageName());
        assertFalse(info.isInterface());
        assertTrue(info.getAnnotations().contains("@RestController"));
        assertEquals(1, info.getFields().size());
        assertEquals(1, info.getMethods().size());
        assertEquals(1, info.getRestEndpoints().size());
    }

    @Test
    void testParseNullOrEmptyCode() {
        JavaCodeParser.ClassInfo info = parser.parseJavaCode("");
        assertNull(info.getName());
    }
}
