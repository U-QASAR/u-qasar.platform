
package eu.uqasar.web.pages.admin.meta;

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


import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 *
 */
class MetaDataInUseIndicatorPanel extends Panel {
    
    public MetaDataInUseIndicatorPanel(final String id, boolean inUse, final IModel<String> objectName) {
        super(id);
        final String determine = inUse ? "yes" : "no";
        add(new Icon("icon", new IconType(inUse ? "check-sign" : "remove-sign")));
        Label label = new Label("text", new StringResourceModel("label."+ determine, this, null));
        label.add(new CssClassNameAppender(inUse ? "error" : "success"));
        add(label);
    }
}
