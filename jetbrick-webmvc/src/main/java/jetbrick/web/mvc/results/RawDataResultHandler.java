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
package jetbrick.web.mvc.results;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import jetbrick.io.IoUtils;
import jetbrick.ioc.annotations.Managed;
import jetbrick.web.mvc.RequestContext;

// 自定义输出
@Managed
public class RawDataResultHandler implements ResultHandler<RawData> {

    @Override
    public void handle(RequestContext ctx, RawData result) throws IOException {
        HttpServletResponse response = ctx.getResponse();

        response.setContentType(result.getContentType());
        if (result.getContentLength() > 0) {
            response.setContentLength(result.getContentLength());
        }

        ServletOutputStream out = response.getOutputStream();
        InputStream is = result.getInputStream();
        try {
            IoUtils.copy(is, out);
        } finally {
            IoUtils.closeQuietly(is);
        }
        out.flush();
    }

}
