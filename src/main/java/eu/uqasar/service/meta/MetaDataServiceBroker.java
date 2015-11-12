package eu.uqasar.service.meta;

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


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import eu.uqasar.model.meta.ContinuousIntegrationTool;
import eu.uqasar.model.meta.CustomerType;
import eu.uqasar.model.meta.IssueTrackingTool;
import eu.uqasar.model.meta.MetaData;
import eu.uqasar.model.meta.ProgrammingLanguage;
import eu.uqasar.model.meta.ProjectType;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.meta.SoftwareDevelopmentMethodology;
import eu.uqasar.model.meta.SoftwareLicense;
import eu.uqasar.model.meta.SoftwareType;
import eu.uqasar.model.meta.SourceCodeManagementTool;
import eu.uqasar.model.meta.StaticAnalysisTool;
import eu.uqasar.model.meta.TestManagementTool;
import eu.uqasar.model.meta.Topic;

/**
 *
 *
 */
@Stateless
public class MetaDataServiceBroker {

    @Inject
    private ContinuousIntegrationToolService continuousIntegrationToolService;
    @Inject
    private CustomerTypeService customerTypeService;
    @Inject
    private IssueTrackingToolService isueTrackingToolService;
    @Inject
    private ProgrammingLanguageService programmingLanguageService;
    @Inject
    private ProjectTypeService projectTypeService;
    @Inject
    private SoftwareLicenseService softwareLicenseService;
    @Inject
    private SoftwareTypeService softwareTypeService;
    @Inject
    private SourceCodeManagementToolService sourceCodeManagementToolService;
    @Inject
    private StaticAnalysisToolService staticAnalysisToolService;
    @Inject
    private TestManagementToolService testManagementToolService;
    @Inject
    private TopicService topicService;
    @Inject
    private SoftwareDevelopmentMethodologyService softwareDevelopmentMethodologyService;
    
    @Inject
    private QModelTagDataService qModelTagDataService;
    
    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;

    
    
    public <T extends MetaData, S extends MetaDataService<T>> S getService(Class<T> clazz) {
        if (ContinuousIntegrationTool.class.isAssignableFrom(clazz)) {
            return (S) continuousIntegrationToolService;
        } else if (CustomerType.class.isAssignableFrom(clazz)) {
            return (S) customerTypeService;
        } else if (IssueTrackingTool.class.isAssignableFrom(clazz)) {
            return (S) isueTrackingToolService;
        } else if (ProgrammingLanguage.class.isAssignableFrom(clazz)) {
            return (S) programmingLanguageService;
        } else if (ProjectType.class.isAssignableFrom(clazz)) {
            return (S) projectTypeService;
        } else if (SoftwareLicense.class.isAssignableFrom(clazz)) {
            return (S) softwareLicenseService;
        } else if (SoftwareType.class.isAssignableFrom(clazz)) {
            return (S) softwareTypeService;
        } else if (SourceCodeManagementTool.class.isAssignableFrom(clazz)) {
            return (S) sourceCodeManagementToolService;
        } else if (StaticAnalysisTool.class.isAssignableFrom(clazz)) {
            return (S) staticAnalysisToolService;
        } else if (TestManagementTool.class.isAssignableFrom(clazz)) {
            return (S) testManagementToolService;
        } else if (Topic.class.isAssignableFrom(clazz)) {
            return (S) topicService;
        } else if (SoftwareDevelopmentMethodology.class.isAssignableFrom(clazz)) {
        	return (S) softwareDevelopmentMethodologyService;
        } else if (QModelTagData.class.isAssignableFrom(clazz)) {
        	return (S) qModelTagDataService;
        }
        
        
        return (S) metaDataService;
    }
}
