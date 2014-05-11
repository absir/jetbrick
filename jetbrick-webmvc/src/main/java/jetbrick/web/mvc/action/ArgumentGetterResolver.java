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
package jetbrick.web.mvc.action;

import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import jetbrick.commons.lang.ExceptionUtils;
import jetbrick.commons.lang.Validate;
import jetbrick.commons.typecast.Convertor;
import jetbrick.commons.typecast.TypeCastUtils;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotations.*;
import jetbrick.web.mvc.Model;
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.action.annotations.*;
import jetbrick.web.mvc.multipart.FilePart;

/**
 * 全局 ArgumentGetter 管理器.
 */
public final class ArgumentGetterResolver {
    private final Map<Class<?>, TypedArgumentGetter<?>> typedMaps = new IdentityHashMap<Class<?>, TypedArgumentGetter<?>>();
    private final Map<Class<?>, Class<AnnotatedArgumentGetter<?, ?>>> annotatedMaps = new IdentityHashMap<Class<?>, Class<AnnotatedArgumentGetter<?, ?>>>();

    @Inject
    private Ioc ioc;

    @IocInit
    public void initialize() {
        // typed
        register(RequestContext.class, RequestContextArgumentGetter.class);
        register(Model.class, ModelArgumentGetter.class);
        register(HttpServletRequest.class, HttpServletRequestArgumentGetter.class);
        register(HttpServletResponse.class, HttpServletResponseArgumentGetter.class);
        register(HttpSession.class, HttpSessionArgumentGetter.class);
        register(ServletContext.class, ServletContextArgumentGetter.class);
        register(FilePart.class, FilePartArgumentGetter.class);
        register(FilePart[].class, FilePartsArgumentGetter.class);

        // annotated
        register(PathVariable.class, PathVariableArgumentGetter.class);
        register(RequestParam.class, RequestParamArgumentGetter.class);
        register(RequestForm.class, RequestFormArgumentGetter.class);
        register(RequestHeader.class, RequestHeaderArgumentGetter.class);
        register(RequestCookie.class, RequestCookieArgumentGetter.class);
        register(RequestAttribute.class, RequestAttributeArgumentGetter.class);
        register(SessionAttribute.class, SessionAttributeArgumentGetter.class);
        register(ServletContextAttribute.class, ServletContextAttributeArgumentGetter.class);
        register(InitParameter.class, InitParameterArgumentGetter.class);
    }

    @SuppressWarnings("unchecked")
    public void register(Class<?> type, Class<?> argumentGetterClass) {
        if (TypedArgumentGetter.class.isAssignableFrom(argumentGetterClass)) {
            // singleton
            try {
                TypedArgumentGetter<?> getter = (TypedArgumentGetter<?>) ioc.injectClass(argumentGetterClass);
                typedMaps.put(type, getter);
            } catch (Exception e) {
                throw ExceptionUtils.unchecked(e);
            }
        } else if (AnnotatedArgumentGetter.class.isAssignableFrom(argumentGetterClass)) {
            annotatedMaps.put(type, (Class<AnnotatedArgumentGetter<?, ?>>) argumentGetterClass);
        } else {
            throw new IllegalStateException("Invalid class " + argumentGetterClass);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> TypedArgumentGetter<T> lookup(Class<T> type) {
        return (TypedArgumentGetter<T>) typedMaps.get(type);
    }

    @SuppressWarnings("rawtypes")
    public <A extends Annotation> Class<? extends AnnotatedArgumentGetter> lookup(A annotation) {
        Validate.notNull(annotation);

        // Special code
        if (annotation.annotationType().isAnnotationPresent(InjectParameterWith.class)) {
            return IocBeanArgumentGetter.class;
        }
        return annotatedMaps.get(annotation.annotationType());
    }

    public static Convertor<?> getTypeConvertor(Class<?> type) {
        if (type == null || type == String.class) {
            return null;
        } else {
            return TypeCastUtils.lookup(type);
        }
    }
}
