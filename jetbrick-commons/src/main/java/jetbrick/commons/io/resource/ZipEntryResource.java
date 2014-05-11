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
package jetbrick.commons.io.resource;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import jetbrick.commons.lang.*;

public class ZipEntryResource extends Resource {
    private final URL url;
    private final ZipFile zip;
    private final ZipEntry entry;
    private final String entryName;

    public static ZipEntryResource create(URL url) {
        String protocol = url.getProtocol();
        if (URL_PROTOCOL_JAR.equals(protocol)) {
            try {
                URLConnection conn = url.openConnection();
                if (conn instanceof JarURLConnection) {
                    JarURLConnection connection = (JarURLConnection) conn;
                    ZipFile zip = connection.getJarFile();
                    ZipEntry entry = connection.getJarEntry();
                    return new ZipEntryResource(url, zip, entry, entry.getName());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (URL_PROTOCOL_ZIP.equals(protocol)) {
            try {
                URLConnection conn = url.openConnection();
                if ("weblogic.utils.zip.ZipURLConnection".equals(conn.getClass().getName())) {
                    ZipFile zip = WeblogicZipURLConnection.getZipFile(conn);
                    ZipEntry entry = WeblogicZipURLConnection.getZipEntry(conn);
                    return new ZipEntryResource(url, zip, entry, entry.getName());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        throw new IllegalStateException("Unknown url format: " + url);
    }

    public static ZipEntryResource create(ZipFile zip, String entryName) {
        Validate.notNull(zip);
        return new ZipEntryResource(null, zip, zip.getEntry(entryName), entryName);
    }

    public static ZipEntryResource create(ZipFile zip, ZipEntry entry) {
        Validate.notNull(zip);
        return new ZipEntryResource(null, zip, entry, entry.getName());
    }

    private ZipEntryResource(URL url, ZipFile zip, ZipEntry entry, String entryName) {
        this.url = url;
        this.zip = zip;
        this.entry = entry;
        this.entryName = entryName;
    }

    public ZipFile getZipFile() {
        return zip;
    }

    public ZipEntry getZipEntry() {
        return entry;
    }

    public String getZipEntryName() {
        return entryName;
    }

    @Override
    public InputStream openStream() {
        try {
            return zip.getInputStream(entry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI getURI() {
        try {
            return getURL().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL getURL() {
        if (url != null) {
            return url;
        }

        try {
            String path = URL_PREFIX_FILE + zip.getName() + URL_SEPARATOR_JAR + entryName;
            return new URL(URL_PROTOCOL_JAR, null, path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exist() {
        return entry != null;
    }

    @Override
    public boolean isDirectory() {
        return entry == null ? false : entry.isDirectory();
    }

    @Override
    public boolean isFile() {
        return entry == null ? false : !entry.isDirectory();
    }

    @Override
    public String getFileName() {
        String name = StringUtils.removeEnd(entryName, "/");
        int slash = name.lastIndexOf('/');
        if (slash >= 0) {
            return name.substring(slash + 1);
        }
        return name;
    }

    @Override
    public long length() {
        return entry == null ? NOT_FOUND : entry.getSize();
    }

    @Override
    public long lastModified() {
        return entry == null ? NOT_FOUND : entry.getTime();
    }

    @Override
    public String toString() {
        if (url != null) {
            return url.toString();
        } else {
            return "jar:" + zip.getName() + URL_SEPARATOR_JAR + entryName;
        }
    }

    static class WeblogicZipURLConnection {
        static final Field FIELD_ZIP_FILE;
        static final Field FIELD_ZIP_ENTRY;

        static {
            ClassLoader loader = WeblogicZipURLConnection.class.getClassLoader();
            try {
                Class<?> klass = loader.loadClass("weblogic.utils.zip.ZipURLConnection");
                FIELD_ZIP_FILE = klass.getDeclaredField("zip");
                FIELD_ZIP_ENTRY = klass.getDeclaredField("ze");

                FIELD_ZIP_FILE.setAccessible(true);
                FIELD_ZIP_ENTRY.setAccessible(true);
            } catch (Exception e) {
                throw new IllegalStateException("Could not detect Weblogic zip url infrastructure", e);
            }
        }

        static ZipFile getZipFile(URLConnection conn) {
            try {
                return (ZipFile) FIELD_ZIP_FILE.get(conn);
            } catch (Exception e) {
                throw ExceptionUtils.unchecked(e);
            }
        }

        static ZipEntry getZipEntry(URLConnection conn) {
            try {
                return (ZipEntry) FIELD_ZIP_ENTRY.get(conn);
            } catch (Exception e) {
                throw ExceptionUtils.unchecked(e);
            }
        }
    }
}
