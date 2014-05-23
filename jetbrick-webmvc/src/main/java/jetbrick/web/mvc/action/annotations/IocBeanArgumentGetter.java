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
package jetbrick.web.mvc.action.annotations;

import java.lang.annotation.Annotation;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotations.*;
import jetbrick.ioc.injectors.ParameterInjector;
import jetbrick.lang.ExceptionUtils;
import jetbrick.lang.Validate;
import jetbrick.web.mvc.RequestContext;

@Managed
public class IocBeanArgumentGetter implements AnnotatedArgumentGetter<Annotation, Object> {
    @Inject
    private Ioc ioc;

    private ParameterInjector injector;

    @Override
    public void initialize(ArgumentContext<Annotation> ctx) {
        Annotation annotation = ctx.getAnnotation();
        InjectParameterWith with = annotation.annotationType().getAnnotation(InjectParameterWith.class);
        Validate.notNull(with, "@InjectParameterWith not found.");

        try {
            injector = with.value().newInstance();
            injector.initialize(ioc, ctx.getDeclaringKlass(), ctx.getParameter(), annotation);
        } catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Override
    public Object get(RequestContext ctx) throws Exception {
        return injector.getObject();
    }
}
