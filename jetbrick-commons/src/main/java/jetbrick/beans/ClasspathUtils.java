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
package jetbrick.beans;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import jetbrick.io.URLUtils;
import jetbrick.lang.StringUtils;

public final class ClasspathUtils {
    public static final String EXT_CLASS_LOADER_NAME = "sun.misc.Launcher$ExtClassLoader";
    public static final String APP_CLASS_LOADER_NAME = "sun.misc.Launcher$AppClassLoader";

    /**
     * 根据 classLoader 获取所有的 Classpath URLs.
     */
    public static Collection<URL> getClasspathURLs(final ClassLoader classLoader) {
        Collection<URL> urls = new LinkedHashSet<URL>(32);
        ClassLoader loader = classLoader;
        while (loader != null) {
            String className = loader.getClass().getName();
            if (EXT_CLASS_LOADER_NAME.equals(className)) {
                break;
            }
            if (loader instanceof URLClassLoader) {
                for (URL url : ((URLClassLoader) loader).getURLs()) {
                    urls.add(url);
                }
            } else if (className.startsWith("weblogic.utils.classloaders.")) {
                // 该死的 WebLogic，只能特殊处理
                // GenericClassLoader, FilteringClassLoader, ChangeAwareClassLoader
                try {
                    Method method = loader.getClass().getMethod("getClassPath");
                    Object result = method.invoke(loader);
                    if (result != null) {
                        String[] paths = StringUtils.split(result.toString(), File.pathSeparatorChar);
                        for (String path : paths) {
                            urls.add(URLUtils.fromFile(path));
                        }
                    }
                } catch (NoSuchMethodException e) {
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (className.startsWith("org.jboss.modules.ModuleClassLoader")) {
                // 该死的 Jboss/Wildfly 8，只能特殊处理
                try {
                    Set<URL> urlSet = JBossModuleUtils.getClasspathURLs(loader, false);
                    urls.addAll(urlSet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            loader = loader.getParent();
        }

        // moved jsp classpath from ServletContext.attributes to System.properties
        String jsp_classpath = System.getProperty("org.apache.catalina.jsp_classpath");
        String classpath = System.getProperty("java.class.path");
        classpath = StringUtils.trimToEmpty(classpath) + File.pathSeparatorChar + StringUtils.trimToEmpty(jsp_classpath);
        if (classpath.length() > 1) {
            String[] paths = StringUtils.split(classpath, File.pathSeparatorChar);
            for (String path : paths) {
                path = path.trim();
                if (path.length() > 0) {
                    URL url = URLUtils.fromFile(path);
                    urls.add(url);
                }
            }
        }

        // 添加包含所有的 META-INF/MANIFEST.MF 的 jar 文件
        try {
            Enumeration<URL> paths = classLoader.getResources("META-INF/MANIFEST.MF");
            while (paths.hasMoreElements()) {
                URL url = paths.nextElement();
                File file = URLUtils.toFileObject(url);
                urls.add(file.toURI().toURL());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 删除 jdk 自带的 jar
        Iterator<URL> it = urls.iterator();
        while (it.hasNext()) {
            String path = it.next().getPath();
            if (path.contains("/jre/lib/")) {
                it.remove();
            }
        }

        return urls;
    }

    /**
     * 根据 classLoader 获取指定 package 对应的 URLs.
     */
    public static Collection<URL> getClasspathURLs(ClassLoader classLoader, String packageName) {
        if (packageName == null) {
            throw new IllegalArgumentException("PackageName must be not null.");
        }
        Collection<URL> urls = new ArrayList<URL>();
        String dirname = packageName.replace('.', '/');
        try {
            Enumeration<URL> dirs = classLoader.getResources(dirname);
            while (dirs.hasMoreElements()) {
                urls.add(dirs.nextElement());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return urls;
    }

}
