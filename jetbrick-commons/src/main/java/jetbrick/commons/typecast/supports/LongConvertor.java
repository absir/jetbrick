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
package jetbrick.commons.typecast.supports;

import jetbrick.commons.typecast.Convertor;
import jetbrick.commons.typecast.TypeCastException;

public final class LongConvertor implements Convertor<Long> {
    public static final LongConvertor INSTANCE = new LongConvertor();

    @Override
    public Long convert(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw TypeCastException.create(value, Long.class, e);
        }
    }

    @Override
    public Long convert(Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass() == Long.class) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        return convert(value.toString());
    }
}
