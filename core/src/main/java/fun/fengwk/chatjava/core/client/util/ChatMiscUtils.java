package fun.fengwk.chatjava.core.client.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author fengwk
 */
public class ChatMiscUtils {

    public static final String EMPTY = "";

    private static final int BUFFER_SIZE = 1024 * 4;

    private static final Map<Class<?>, Class<?>> BOXED_MAP;

    static {
        Map<Class<?>, Class<?>> boxedMap = new HashMap<>();
        boxedMap.put(byte.class, Byte.class);
        boxedMap.put(short.class, Short.class);
        boxedMap.put(int.class, Integer.class);
        boxedMap.put(long.class, Long.class);
        boxedMap.put(float.class, Float.class);
        boxedMap.put(double.class, Double.class);
        boxedMap.put(char.class, Character.class);
        boxedMap.put(boolean.class, Boolean.class);
        BOXED_MAP = boxedMap;
    }

    private ChatMiscUtils() {
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static <T> Collection<T> nullSafe(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }

    public static <T> List<T> nullSafe(List<T> list, Supplier<List<T>> defaultSupplier) {
        return list == null ? defaultSupplier.get() : list;
    }

    public static String nullSafe(String str) {
        return str == null ? EMPTY : str;
    }

    public static <S, T> T nullSafeMap(S obj, Function<S, T> mapper) {
        return obj == null ? null : mapper.apply(obj);
    }

    public static <E> E tryGetLast(List<E> list) {
        return list == null || list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public static String toString(InputStream input, Charset charset) throws IOException {
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream()) {
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = input.read(buf)) != -1) {
                bytesOut.write(buf, 0, len);
            }
            return bytesOut.toString(charset);
        }
    }

    public static Set<Method> getAllDeclaredMethods(Class<?> clazz) {
        Set<Method> allDeclaredMethods = new HashSet<>();
        while (clazz != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            allDeclaredMethods.addAll(Arrays.asList(declaredMethods));
            clazz = clazz.getSuperclass();
        }
        return allDeclaredMethods;
    }

    public static Set<Field> getAllDeclaredFields(Class<?> clazz) {
        Set<Field> allDeclaredFields = new HashSet<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            allDeclaredFields.addAll(Arrays.asList(declaredFields));
            clazz = clazz.getSuperclass();
        }
        return allDeclaredFields;
    }

    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement,
                                                          Class<A> annotationType) {
        return doFindAnnotation(annotatedElement, annotationType, new HashSet<>());
    }

    public static <A extends Annotation> A doFindAnnotation(AnnotatedElement annotatedElement,
                                                            Class<A> annotationType,
                                                            Set<Class<? extends Annotation>> visited) {
        Annotation[] annotations = annotatedElement.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (visited.add(annotation.annotationType())) {
                if (annotation.annotationType() == annotationType) {
                    return annotationType.cast(annotation);
                }
                A superAnno = doFindAnnotation(annotation.annotationType(), annotationType, visited);
                if (superAnno != null) {
                    return superAnno;
                }
            }
        }
        return null;
    }

    public static Object invokeMethod(Method method, Object target, Object[] params) {
        try {
            return method.invoke(target, params);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    public static Class<?> boxedIfPrimitiveType(Class<?> clazz) {
        Class<?> packClass = BOXED_MAP.get(clazz);
        if (packClass != null) {
            return packClass;
        }
        return clazz;
    }

    private static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            rethrowRuntimeException(ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    private static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

}
