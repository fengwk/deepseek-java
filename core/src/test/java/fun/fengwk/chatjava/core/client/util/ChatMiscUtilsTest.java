package fun.fengwk.chatjava.core.client.util;

import fun.fengwk.chatjava.core.client.response.ChatLogprobContent;
import org.apiguardian.api.API;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class ChatMiscUtilsTest {

    @Test
    public void testIoToString() throws IOException {
        String str = "qwertyuiopasdfghjkl";
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))) {
            String str2 = ChatMiscUtils.toString(bytesIn, StandardCharsets.UTF_8);
            Assertions.assertEquals(str, str2);
        }
    }

    @Test
    public void testGetAllDeclaredMethods() {
        Set<String> allDeclaredMethodNames = ChatMiscUtils.getAllDeclaredMethods(ChatMiscUtilsTest.class).stream()
            .map(Method::getName).collect(Collectors.toSet());
        Assertions.assertTrue(allDeclaredMethodNames.contains("testGetAllDeclaredMethods"));
        Assertions.assertTrue(allDeclaredMethodNames.contains("toString"));
        Assertions.assertTrue(allDeclaredMethodNames.contains("hashCode"));
    }

    @Test
    public void testGetAllDeclaredFields() {
        Set<String> allDeclaredFieldNames = ChatMiscUtils.getAllDeclaredFields(ChatLogprobContent.class).stream()
            .map(Field::getName).collect(Collectors.toSet());
        Assertions.assertTrue(allDeclaredFieldNames.contains("top_logprobs"));
        Assertions.assertTrue(allDeclaredFieldNames.contains("token"));
        Assertions.assertTrue(allDeclaredFieldNames.contains("logprob"));
        Assertions.assertTrue(allDeclaredFieldNames.contains("bytes"));
    }

    @Test
    public void testFindAnnotation() throws NoSuchMethodException {
        Method testFindAnnotation = ChatMiscUtilsTest.class.getMethod("testFindAnnotation");
        Assertions.assertNotNull(ChatMiscUtils.findAnnotation(testFindAnnotation, Test.class));
        Assertions.assertNotNull(ChatMiscUtils.findAnnotation(testFindAnnotation, Target.class));
        Assertions.assertNotNull(ChatMiscUtils.findAnnotation(testFindAnnotation, API.class));
    }

}
