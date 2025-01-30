package fun.fengwk.chatjava.core.client.tool;

import fun.fengwk.chatjava.core.client.tool.annotation.ToolFunction;
import fun.fengwk.chatjava.core.client.tool.annotation.ToolFunctionParam;
import fun.fengwk.chatjava.core.client.util.ChatMiscUtils;
import fun.fengwk.chatjava.core.client.util.relfect.TypeResolver;

import java.lang.reflect.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 解析带有{@link ToolFunction}注解的函数
 *
 * @author fengwk
 */
public class ToolFunctionParser {

    public List<ToolFunctionHandler> parse(Object object) {
        List<ToolFunctionHandler> handlers = new ArrayList<>();
        doParse(object, handlers);
        return handlers;
    }

    private void doParse(Object object, List<ToolFunctionHandler> handlers) {
        for (Method method : ChatMiscUtils.getAllDeclaredMethods(object.getClass())) {
            ToolFunction toolFunction = ChatMiscUtils.findAnnotation(method, ToolFunction.class);
            if (toolFunction == null) {
                continue;
            }

            if (!CharSequence.class.isAssignableFrom(method.getReturnType())) {
                throw new IllegalStateException("@ToolFunction should return CharSequence type");
            }

            ReflectToolFunctionHandler toolFunctionHandler = new ReflectToolFunctionHandler();
            toolFunctionHandler.setName(method.getDeclaringClass().getSimpleName() + "_" + method.getName());
            toolFunctionHandler.setDescription(toolFunction.description());
            toolFunctionHandler.setParameters(parseParameters(method));
            toolFunctionHandler.setMethod(method);
            toolFunctionHandler.setTarget(object);
            handlers.add(toolFunctionHandler);
        }
    }

    private JsonSchema parseParameters(Method method) {
        JsonSchema root = new JsonSchema();
        root.setType("object");
        root.setProperties(new LinkedHashMap<>());
        root.setRequired(new ArrayList<>());
        for (Parameter parameter : method.getParameters()) {
            ToolFunctionParam toolFunctionParam = ChatMiscUtils.findAnnotation(parameter, ToolFunctionParam.class);
            if (toolFunctionParam == null) {
                throw new IllegalStateException(String.format("%s must be annotated with @ToolFunctionParam",
                    String.format("%s->%s", method.getName(), parameter.getName())));
            }

            String name = toolFunctionParam.name();
            root.getProperties().put(name, parseParameter(parameter.getParameterizedType(),
                String.format("%s->%s", method.getName(), name)));
            if (toolFunctionParam.required()) {
                root.getRequired().add(name);
            }
        }
        return root;
    }

    private JsonSchema parseParameter(Type type, String pathTrace) {
        JsonSchema jsonSchema = new JsonSchema();
        jsonSchema.setJavaType(type);

        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            clazz = ChatMiscUtils.boxedIfPrimitiveType(clazz);
            if (Byte.class.isAssignableFrom(clazz)
                || Short.class.isAssignableFrom(clazz)
                || Integer.class.isAssignableFrom(clazz)
                || Long.class.isAssignableFrom(clazz)
                || BigInteger.class.isAssignableFrom(clazz)
                || AtomicInteger.class.isAssignableFrom(clazz)
                || AtomicLong.class.isAssignableFrom(clazz)
                || LongAdder.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("integer");
            } else if (Number.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("number");
            } else if (CharSequence.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("string");
            } else if (Boolean.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("boolean");
            } else if (Collection.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("array");
                JsonSchema any = new JsonSchema();
                any.setType("any");
                jsonSchema.setItems(any);
            } else if (clazz.isArray()) {
                jsonSchema.setType("array");
                jsonSchema.setItems(parseParameter(clazz.getComponentType(), pathTrace + "[]"));
            } else {
                jsonSchema.setType("object");
                jsonSchema.setProperties(new LinkedHashMap<>());
                for (Field field : ChatMiscUtils.getAllDeclaredFields(clazz)) {
                    jsonSchema.getProperties().put(field.getName(),
                        parseParameter(field.getGenericType(), pathTrace + "->" + field.getName()));
                }
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type rawType = pt.getRawType();
            if (rawType instanceof Class<?> && Collection.class.isAssignableFrom((Class<?>) rawType)) {
                ParameterizedType collectionPt = new TypeResolver(type).as(Collection.class).asParameterizedType();
                jsonSchema.setType("array");
                jsonSchema.setItems(parseParameter(collectionPt.getActualTypeArguments()[0], pathTrace + "[]"));
            } else {
                jsonSchema.setType("any");
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType) type;
            jsonSchema.setType("array");
            jsonSchema.setItems(parseParameter(gat.getGenericComponentType(), pathTrace + "[]"));
        } else {
            jsonSchema.setType("any");
        }

        return jsonSchema;
    }

}
