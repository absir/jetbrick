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
import jetbrick.ioc.annotations.Managed;
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.WebException;

@Managed
public class HttpStatusResultHandler implements ResultHandler<HttpStatus> {

    @Override
    public void handle(RequestContext ctx, HttpStatus result) throws Exception {
        int status = result.getStatus();
        if (status >= 400) {
            try {
                String message = result.getMessage();
                if (message == null) {
                    ctx.getResponse().sendError(status);
                } else {
                    ctx.getResponse().sendError(status, message);
                }
            } catch (IOException e) {
                throw WebException.uncheck(e);
            }
        } else {
            ctx.getResponse().setStatus(status);
        }
    }
}
