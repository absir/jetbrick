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
package jetbrick.web.mvc;

import java.util.HashMap;
import java.util.Map;
import jetbrick.lang.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.*;

@SuppressWarnings("serial")
public class Model extends HashMap<String, Object> {
    public static final String NAME_IN_REQUEST = Model.class.getName();

    public Object add(String name, Object value) {
        if (value == null) {
            return super.remove(name);
        } else {
            return super.put(name, value);
        }
    }

    @Override
    public Object put(String name, Object value) {
        if (value == null) {
            return super.remove(name);
        } else {
            return super.put(name, value);
        }
    }

    /**
     * 使用内置的 json 库，转成 json 字符串
     */
    public String toJSONString() {
        return JSONUtils.toJSONString(this);
    }

    /**
     * 转成 fastjson 对象
     */
    public JSONObject toFastjson() {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, Object> entry : entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json;
    }

    /**
     * 转成 gson 对象
     */
    public JsonElement toGson() {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        for (Map.Entry<String, Object> entry : entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            json.add(key, gson.toJsonTree(value));
        }
        return json;
    }
}
