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
package jetbrick.commons.io.finder;

import java.lang.annotation.Annotation;
import java.util.*;
import jetbrick.commons.beans.ClassLoaderUtils;
import jetbrick.commons.lang.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassFinder {
    private static final Logger log = LoggerFactory.getLogger(ClassFinder.class);

    public static Set<Class<?>> getClasses(Class<? extends Annotation>[] annotations, boolean skiperrors) {
        return getClasses((String[]) null, true, annotations, skiperrors);
    }

    @SuppressWarnings("unchecked")
    public static Set<Class<?>> getClasses(Collection<String> packageNames, boolean recursive, Collection<Class<? extends Annotation>> annotations, boolean skiperrors) {
        String[] pkgs = packageNames.toArray(new String[packageNames.size()]);
        Class<? extends Annotation>[] annos = annotations.toArray(new Class[annotations.size()]);
        return getClasses(pkgs, recursive, annos, skiperrors);
    }

    public static Set<Class<?>> getClasses(String[] packageNames, boolean recursive, Class<? extends Annotation>[] annotations, final boolean skiperrors) {
        final AnnotationClassReader reader = new AnnotationClassReader();
        for (Class<? extends Annotation> annotation : annotations) {
            reader.addAnnotation(annotation);
        }

        final ClassLoader loader = ClassLoaderUtils.getDefault();
        final Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

        FileFinder finder = new FileFinder() {
            @Override
            public void visitFile(ResourceEntry file) {
                if (file.isJavaClass()) {
                    if (reader.isAnnotationed(file.openStream())) {
                        addClass(file.getQualifiedJavaName());
                    }
                }
            }

            private void addClass(String qualifiedClassName) {
                try {
                    Class<?> klass = loader.loadClass(qualifiedClassName);
                    classes.add(klass);
                } catch (ClassNotFoundException e) {
                } catch (Throwable e) {
                    if (skiperrors) {
                        log.warn("Class load error.", e);
                    } else {
                        throw ExceptionUtils.unchecked(e);
                    }
                }
            }
        };

        finder.lookupClasspath(packageNames, recursive);

        return classes;
    }
}
