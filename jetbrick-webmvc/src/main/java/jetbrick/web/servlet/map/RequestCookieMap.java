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
package jetbrick.web.servlet.map;

import java.util.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RequestCookieMap implements Map<String, Cookie> {
    private final HttpServletRequest request;
    private Map<String, Cookie> map;

    public RequestCookieMap(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean containsKey(Object key) {
        return getAsMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getAsMap().containsValue(value);
    }

    @Override
    public Cookie get(Object key) {
        return getAsMap().get(key);
    }

    @Override
    public boolean isEmpty() {
        return getAsMap().isEmpty();
    }

    @Override
    public int size() {
        return getAsMap().size();
    }

    @Override
    public Set<Map.Entry<String, Cookie>> entrySet() {
        return getAsMap().entrySet();
    }

    @Override
    public Set<String> keySet() {
        return getAsMap().keySet();
    }

    @Override
    public Collection<Cookie> values() {
        return getAsMap().values();
    }

    @Override
    public synchronized Cookie put(String key, Cookie value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void putAll(Map<? extends String, ? extends Cookie> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized Cookie remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void clear() {
        throw new UnsupportedOperationException();
    }

    protected Map<String, Cookie> getAsMap() {
        if (map == null) {
            synchronized (this) {
                if (map == null) {
                    Cookie[] cookies = request.getCookies();
                    if (cookies == null) {
                        map = Collections.emptyMap();
                    } else {
                        Map<String, Cookie> result = new HashMap<String, Cookie>();
                        for (Cookie cookie : cookies) {
                            String name = cookie.getName();
                            result.put(name, cookie);
                        }
                        map = result;
                    }
                }
            }
        }
        return map;
    }
}
