package fun.fengwk.chatjava.core.client.util.relfect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public final class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;

    public GenericArrayTypeImpl(Type genericComponentType) {
        assert genericComponentType != null;
        this.genericComponentType = genericComponentType;
    }

    @Override
    public Type getGenericComponentType() {
        return this.genericComponentType;
    }

    @Override
    public String toString() {
        Type genericComponentType = this.getGenericComponentType();
        StringBuilder builder = new StringBuilder();
        if (genericComponentType instanceof Class) {
            builder.append(((Class<?>) genericComponentType).getName());
        } else {
            builder.append(genericComponentType.toString());
        }
        builder.append("[]");
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericArrayType) {
            GenericArrayType that = (GenericArrayType) obj;
            return this.genericComponentType.equals(that.getGenericComponentType());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.genericComponentType.hashCode();
    }
}
