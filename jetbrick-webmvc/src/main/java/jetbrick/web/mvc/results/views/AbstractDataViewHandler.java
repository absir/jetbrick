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
package jetbrick.web.mvc.results.views;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrick.web.mvc.RequestContext;

// 所有子类都是单例
public abstract class AbstractDataViewHandler implements ViewHandler {

    @Override
    public String getSuffix() {
        return null;
    }

    public abstract String getMimetype(HttpServletRequest request);

    @Override
    public void render(RequestContext ctx, String value) throws IOException {
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        String characterEncoding = request.getCharacterEncoding();
        response.setCharacterEncoding(characterEncoding);
        response.setContentType(getMimetype(request) + "; charset=" + characterEncoding);

        PrintWriter out = response.getWriter();
        out.write(value);
        out.flush();
    }

}
