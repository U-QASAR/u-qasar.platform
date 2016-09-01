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


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.util.Attributes;
import static eu.uqasar.web.components.util.AjaxBootstrapTabbedPanel.Direction.ABOVE;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

/**
 *
 *
 * @param <T>
 */
@Setter
@Getter
public class AjaxBootstrapTabbedPanel<T extends ITab> extends AjaxTabbedPanel<T> {

    private Direction direction = ABOVE;
    
    AjaxBootstrapTabbedPanel(String id, List<T> tabs) {
        super(id, tabs);
        commonInit();
    }

    AjaxBootstrapTabbedPanel(String id, List<T> tabs, IModel<Integer> model) {
        super(id, tabs, model);
        commonInit();
    }

    /**
     * common initializer
     */
    private void commonInit() {
        BootstrapBaseBehavior.addTo(this);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        checkComponentTag(tag, "div");
        Attributes.addClass(tag, "tabbable");
        if(direction != null && direction != ABOVE) {
            Attributes.addClass(tag, "tabs-" + direction.name().toLowerCase());
        }
    }

    @Override
    protected String getSelectedTabCssClass() {
        return "active";
    }

    @Override
    protected String getLastTabCssClass() {
        return "";
    }
    
    public enum Direction {
        
        LEFT,
        
        RIGHT,
        
        BELOW,
        
        ABOVE
    }
}
