/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 * Email: subchen@gmail.com
 * URL: http://subchen.github.io/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrick.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import jetbrick.beans.TypeResolverUtils;
import jetbrick.lang.IdentifiedNameUtils;
import jetbrick.reflect.asm.ASMFactory;

public final class MethodInfo implements Executable, Comparable<MethodInfo> {
    private final KlassInfo declaringKlass;
    private final Method method;
    private final int offset;

    public static MethodInfo create(Method method) {
        KlassInfo klass = KlassInfo.create(method.getDeclaringClass());
        return klass.getDeclaredMethod(method);
    }

    protected MethodInfo(KlassInfo declaringKlass, Method method, int offset) {
        this.declaringKlass = declaringKlass;
        this.method = method;
        this.offset = offset;
        method.setAccessible(true);
    }

    @Override
    public KlassInfo getDeclaringKlass() {
        return declaringKlass;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    private ParameterInfo[] parameters;

    @Override
    public ParameterInfo[] getParameters() {
        if (parameters == null) {
            synchronized (this) {
                if (parameters == null) {
                    parameters = ExecutableUtils.getParameterInfo(this);
                }
            }
        }
        return parameters;
    }

    @Override
    public int getParameterCount() {
        return method.getParameterTypes().length;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    @Override
    public Type[] getGenericParameterTypes() {
        return method.getGenericParameterTypes();
    }

    @Override
    public boolean isVarArgs() {
        return method.isVarArgs();
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public Type getGenericReturnType() {
        return method.getGenericReturnType();
    }

    public Class<?> getRawReturnType(Class<?> declaringKlass) {
        return TypeResolverUtils.getRawType(method.getGenericReturnType(), declaringKlass);
    }

    public Class<?> getRawReturnComponentType(Class<?> declaringKlass, int componentIndex) {
        return TypeResolverUtils.getComponentType(method.getGenericReturnType(), declaringKlass, componentIndex);
    }

    @Override
    public Annotation[] getAnnotations() {
        return method.getAnnotations();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

    @Override
    public Annotation[][] getParameterAnnotations() {
        return method.getParameterAnnotations();
    }

    @Override
    public int getModifiers() {
        return method.getModifiers();
    }

    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    public boolean isReadMethod() {
        Class<?> resultType = method.getReturnType();
        if (method.getParameterTypes().length == 0 && resultType != Void.TYPE) {
            String name = method.getName();
            if (name.length() > 3 && name.startsWith("get")) {
                return true;
            }
            if (name.length() > 2 && name.startsWith("is")) {
                return resultType == Boolean.TYPE || resultType == Boolean.class;
            }
        }
        return false;
    }

    public boolean isWriteMethod() {
        Class<?> resultType = method.getReturnType();
        if (method.getParameterTypes().length == 1 && resultType == Void.TYPE) {
            String name = method.getName();
            if (name.length() > 3 && name.startsWith("set")) {
                return true;
            }
        }
        return false;
    }

    public String getPropertyName() {
        if (!isReadMethod() && !isWriteMethod()) {
            throw new IllegalStateException("method is not a getter/setter: " + this.toString());
        }

        String name = method.getName();
        if (name.startsWith("get")) {
            name = name.substring(3);
        } else if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("set")) {
            name = name.substring(3);
        }
        return IdentifiedNameUtils.decapitalize(name);
    }

    public Object invoke(Object object, Object... args) {
        if (ASMFactory.IS_ASM_ENABLED) {
            return declaringKlass.getMethodAccessor().invoke(object, offset, args);
        } else {
            try {
                return method.invoke(object, args);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String descriptor;

    @Override
    public String getDescriptor() {
        if (descriptor == null) {
            descriptor = ExecutableUtils.getDescriptor(this);
        }
        return descriptor;
    }

    @Override
    public int compareTo(MethodInfo o) {
        return getDescriptor().compareTo(o.getDescriptor());
    }

    @Override
    public String toString() {
        return getDescriptor();
    }
}
