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

public class ValueConstants {

    public static final boolean SINGLETONE = true;

    public static final boolean REQUIRED = true;

    public static final String EMPTY = "";

    public static final String NULL = "\0\1\2";

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNull(String value) {
        return value == null || NULL.equals(value);
    }

    public static String defaultValue(String value, String defaultValue) {
        if (value == null || value.length() == 0 || NULL.equals(value)) {
            return defaultValue;
        }
        return value;
    }

    public static String defaultIfEmpty(String value, String defaultValue) {
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }

    public static String defaultIfNull(String value, String defaultValue) {
        if (value == null || NULL.equals(value)) {
            return defaultValue;
        }
        return value;
    }

    public static String defaultIfNull(String value) {
        if (NULL.equals(value)) {
            return null;
        }
        return value;
    }
}
