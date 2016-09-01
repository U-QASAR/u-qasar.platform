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


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import eu.uqasar.model.meta.MetaData;
import eu.uqasar.service.meta.MetaDataServiceBroker;
import eu.uqasar.web.components.util.AjaxBootstrapTabbedPanel;
import eu.uqasar.web.components.util.AjaxBootstrapTabbedPillsPanel;
import eu.uqasar.web.pages.admin.AdminBasePage;

/**
 *
 *
 */
public class MetaDataSettingsPage extends AdminBasePage {

    @Inject
    MetaDataServiceBroker serviceBroker;
    
    private final AjaxBootstrapTabbedPanel<ITab> tabPanel;

    public MetaDataSettingsPage(PageParameters pageParameters) {
        super(pageParameters);
        List<ITab> tabs = new ArrayList<>();
        for (final Class clazz : MetaData.getAllClasses()) {
            tabs.add(new AbstractTab(Model.of(MetaData.getLabel(clazz))) {
                @Override
                public WebMarkupContainer getPanel(String panelId) {
                    return new MetaDataEditPanel<>(panelId, clazz, serviceBroker.getService(clazz), feedbackPanel);
                }
            });
        }
        AjaxBootstrapTabbedPillsPanel tmp = new AjaxBootstrapTabbedPillsPanel<>("tabs", tabs);
        tmp.setAdjustPillsForMobile(true);
        tabPanel = tmp;
        add(tabPanel);
    }
}
