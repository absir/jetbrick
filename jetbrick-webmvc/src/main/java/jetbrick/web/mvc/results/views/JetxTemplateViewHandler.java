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
import java.io.OutputStream;
import jetbrick.io.ResourceNotFoundException;
import jetbrick.ioc.annotations.Config;
import jetbrick.ioc.annotations.Managed;
import jetbrick.template.*;
import jetbrick.template.web.JetWebContext;
import jetbrick.template.web.JetWebEngineLoader;
import jetbrick.web.mvc.RequestContext;

@Managed
public class JetxTemplateViewHandler extends AbstractTemplateViewHandler {
    @Config(value = "web.view.jetx.prefix", required = false)
    private String prefix;
    @Config(value = "web.view.jetx.suffix", defaultValue = ".jetx")
    private String suffix;

    private JetEngine engine = null;

    @Override
    public String getType() {
        return "jetx";
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    protected boolean doRender(RequestContext ctx, String viewPathName) throws IOException {
        if (engine == null) {
            if (JetWebEngineLoader.unavailable()) {
                JetWebEngineLoader.setServletContext(ctx.getServletContext());
            }
            engine = JetWebEngineLoader.getJetEngine();
            suffix = engine.getConfig().getTemplateSuffix();
        }

        JetContext context = new JetWebContext(ctx.getRequest(), ctx.getResponse(), ctx.getModel());
        OutputStream out = ctx.getResponse().getOutputStream();

        try {
            JetTemplate template = engine.getTemplate(viewPathName);
            template.render(context, out);
        } catch (ResourceNotFoundException e) {
            return false;
        }

        out.flush();
        return true;
    }
}
