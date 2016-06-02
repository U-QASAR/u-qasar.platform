
package eu.uqasar.web.components.util;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.List;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 *
 *
 * @param <T>
 */
public class AjaxBootstrapTabbedPillsPanel<T extends ITab> extends AjaxBootstrapTabbedPanel<T> {
    
    private boolean adjustPillsForMobile = false;

    public AjaxBootstrapTabbedPillsPanel(String id, List<T> tabs) {
        super(id, tabs);
    }

    public AjaxBootstrapTabbedPillsPanel(String id, List<T> tabs, IModel<Integer> model) {
        super(id, tabs, model);
    }

    public boolean getAdjustPillsForMobile() {
        return adjustPillsForMobile;
    }

    public AjaxBootstrapTabbedPillsPanel<T> setAdjustPillsForMobile(boolean adjustPillsForMobile) {
        this.adjustPillsForMobile = adjustPillsForMobile;
        return this;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer script = new WebMarkupContainer("script");
        script.setVisible(adjustPillsForMobile);
        add(script);
    }
}
