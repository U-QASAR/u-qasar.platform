package eu.uqasar.web.pages.user.panels;

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


import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.Select2MultiChoice;

import eu.uqasar.model.meta.ContinuousIntegrationTool;
import eu.uqasar.model.meta.CustomerType;
import eu.uqasar.model.meta.IssueTrackingTool;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.model.meta.ProgrammingLanguage;
import eu.uqasar.model.meta.ProjectType;
import eu.uqasar.model.meta.SoftwareDevelopmentMethodology;
import eu.uqasar.model.meta.SoftwareLicense;
import eu.uqasar.model.meta.SoftwareType;
import eu.uqasar.model.meta.SourceCodeManagementTool;
import eu.uqasar.model.meta.StaticAnalysisTool;
import eu.uqasar.model.meta.TestManagementTool;
import eu.uqasar.model.meta.Topic;
import eu.uqasar.model.user.User;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.web.components.StringResourceModelPlaceholderDelegate;
import eu.uqasar.web.provider.meta.MetaDataCreateMissingEntitiesChoiceProvider;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 *
 */
public class UserSkillsPanel extends Panel {

    private final User user;

    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;

    public UserSkillsPanel(final String id, final User usr) {
        super(id);
        this.user = usr;
        add(newSelect2("programmingLanguages", ProgrammingLanguage.class,
                new PropertyModel<Collection<ProgrammingLanguage>>(user, "knownProgrammingLanguages")));
        add(newSelect2("issueTrackingTools", IssueTrackingTool.class,
                new PropertyModel<Collection<IssueTrackingTool>>(user, "knownIssueTrackingTools")));
        add(newSelect2("testManagementTools", TestManagementTool.class,
                new PropertyModel<Collection<TestManagementTool>>(user, "knownTestManagementTools")));
        add(newSelect2("sourceCodeManagementTools", SourceCodeManagementTool.class,
                new PropertyModel<Collection<SourceCodeManagementTool>>(user, "knownSourceCodeManagementTools")));
        add(newSelect2("staticAnalysisTools", StaticAnalysisTool.class,
                new PropertyModel<Collection<StaticAnalysisTool>>(user, "knownStaticAnalysisTools")));
        add(newSelect2("continuousIntegrationTools", ContinuousIntegrationTool.class,
                new PropertyModel<Collection<ContinuousIntegrationTool>>(user, "knownContinuousIntegrationTools")));
        
        
        
        add(newSelect2("customerType", CustomerType.class, new PropertyModel<Collection<CustomerType>>(user, "customerTypes")));
        add(newSelect2("projectType", ProjectType.class, new PropertyModel<Collection<ProjectType>>(user, "projectTypes")));
        add(newSelect2("softwareType", SoftwareType.class, new PropertyModel<Collection<SoftwareType>>(user, "softwareTypes")));
        add(newSelect2("softwareLicense", SoftwareLicense.class, new PropertyModel<Collection<SoftwareLicense>>(user, "softwareLicenses")));
        add(newSelect2("topic", Topic.class, new PropertyModel<Collection<Topic>>(user, "topics")));
        add(newSelect2("methodology", SoftwareDevelopmentMethodology.class, 
        		new PropertyModel<Collection<SoftwareDevelopmentMethodology>>(user, "softwareDevelopmentMethodologies")));
    }
    
    /**
     * 
     * @param id
     * @param clazz
     * @param model
     * @return
     */
    private <T extends MetaData> Select2Choice<T> newSelect2Single(final String id, Class<T> clazz, PropertyModel<T> model){
    	 return new Select2Choice<>(
                 id, model, new MetaDataCreateMissingEntitiesChoiceProvider<>(
                 metaDataService.getAll(clazz), clazz));
    }
    
    private <T extends MetaData> Select2MultiChoice<T> newSelect2(final String id,
            Class<T> clazz, PropertyModel<Collection<T>> model) {
        Select2MultiChoice<T> select2MultiChoice = new Select2MultiChoice<>(
                id, model, new MetaDataCreateMissingEntitiesChoiceProvider<>(
                        metaDataService.getAll(clazz), clazz));
        final IModel<String> placeHolder = new StringResourceModelPlaceholderDelegate("placeholder.meta.input", this, null, MetaData.getLabelModel(clazz));
        select2MultiChoice.getSettings().setCloseOnSelect(false);
        select2MultiChoice.getSettings().setPlaceholder(placeHolder);
        select2MultiChoice.getSettings().setTokenSeparators(new String[]{","});
        select2MultiChoice.getSettings().setCreateSearchChoice(
                "function(term) { if (term.length > 1) { return { id: term, text: term }; } }");
        add(new Label("label." + id, new StringResourceModel("label.meta.known", this, null, MetaData.getLabelModel(clazz))));
        add(new Label("help." + id, new StringResourceModel("help.meta.input", this, null, MetaData.getLabelModel(clazz))));
        return select2MultiChoice;
    }

}
