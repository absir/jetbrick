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
package jetbrick.typecast.supports;

import java.io.File;
import java.net.*;
import java.nio.file.Path;
import jetbrick.io.resource.FileSystemResource;
import jetbrick.io.resource.Resource;
import jetbrick.lang.JdkVersion;
import jetbrick.typecast.Convertor;

public final class ResourceConvertor implements Convertor<Resource> {
    public static final ResourceConvertor INSTANCE = new ResourceConvertor();

    @Override
    public Resource convert(String value) {
        if (value == null) {
            return null;
        }
        return Resource.create(value);
    }

    @Override
    public Resource convert(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Resource) {
            return (Resource) value;
        }
        if (value instanceof String) {
            return Resource.create((String) value);
        }
        if (value instanceof File) {
            return new FileSystemResource((File) value);
        }
        if (value.getClass() == URL.class) {
            return Resource.create((URL) value);
        }
        if (value.getClass() == URI.class) {
            try {
                return Resource.create(((URI) value).toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        if (JdkVersion.IS_AT_LEAST_JAVA_7) {
            if (value instanceof Path) {
                return new FileSystemResource(((Path) value).toFile());
            }
        }
        return convert(value.toString());
    }
}
