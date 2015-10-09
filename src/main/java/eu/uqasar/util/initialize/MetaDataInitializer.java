package eu.uqasar.util.initialize;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.uqasar.model.meta.ContinuousIntegrationTool;
import eu.uqasar.model.meta.CustomerType;
import eu.uqasar.model.meta.IssueTrackingTool;
import eu.uqasar.model.meta.ProgrammingLanguage;
import eu.uqasar.model.meta.ProjectType;
import eu.uqasar.model.meta.SoftwareDevelopmentMethodology;
import eu.uqasar.model.meta.SoftwareLicense;
import eu.uqasar.model.meta.SoftwareType;
import eu.uqasar.model.meta.SourceCodeManagementTool;
import eu.uqasar.model.meta.StaticAnalysisTool;
import eu.uqasar.model.meta.TestManagementTool;
import eu.uqasar.model.meta.Topic;
import eu.uqasar.service.meta.MetaDataService;

/**
 *
 *
 */
@ApplicationScoped
@Singleton
public class MetaDataInitializer {

    @Inject @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;

    public void initialize() {
        createContinuousIntegrationTools();
        createCustomerTypes();
        createIssueTrackingTools();
        createProgrammingLanguages();
        createProjectTypes();
        createSoftwareLicenses();
        createSoftwareTypes();
        createSourceCodeManagementTools();
        createStaticAnalysisTools();
        createTestManagementTools();
        createTopics();
        createSoftwareDevelopmentMethodologies();
    }

    private void createContinuousIntegrationTools() {
        metaDataService.create(new ContinuousIntegrationTool("Bamboo"));
        metaDataService.create(new ContinuousIntegrationTool("BuildBot"));
        metaDataService.create(new ContinuousIntegrationTool("CruiseControl"));
        metaDataService.create(new ContinuousIntegrationTool("Jenkins/Hudson"));
        metaDataService.
                create(new ContinuousIntegrationTool("Team Foundation Server"));
    }

    private void createCustomerTypes() {
        metaDataService.create(new CustomerType("Regular Customer"));
        metaDataService.create(new CustomerType("Important Customer"));
        metaDataService.create(new CustomerType("Negligible Customer"));
    }

    private void createIssueTrackingTools() {
        metaDataService.create(new IssueTrackingTool("JIRA"));
        metaDataService.create(new IssueTrackingTool("MantisBT"));
        metaDataService.create(new IssueTrackingTool("Bugzilla"));
        metaDataService.create(new IssueTrackingTool("Trac"));
        metaDataService.create(new IssueTrackingTool("Redmine"));
        metaDataService.create(new IssueTrackingTool("Team Foundation Server"));
    }

    private void createProgrammingLanguages() {
        metaDataService.create(new ProgrammingLanguage("ActionScript"));
        metaDataService.create(new ProgrammingLanguage("C"));
        metaDataService.create(new ProgrammingLanguage("C#"));
        metaDataService.create(new ProgrammingLanguage("C++"));
        metaDataService.create(new ProgrammingLanguage("Dart"));
        metaDataService.create(new ProgrammingLanguage("Delphi"));
        metaDataService.create(new ProgrammingLanguage("Java"));
        metaDataService.create(new ProgrammingLanguage("JavaScript"));
        metaDataService.create(new ProgrammingLanguage("Lisp"));
        metaDataService.create(new ProgrammingLanguage("Objective-C"));
        metaDataService.create(new ProgrammingLanguage("Pascal"));
        metaDataService.create(new ProgrammingLanguage("Perl"));
        metaDataService.create(new ProgrammingLanguage("PHP"));
        metaDataService.create(new ProgrammingLanguage("Python"));
        metaDataService.create(new ProgrammingLanguage("Ruby"));
        metaDataService.create(new ProgrammingLanguage("Swift"));
        metaDataService.create(new ProgrammingLanguage("Visual Basic"));
        metaDataService.create(new ProgrammingLanguage("Visual Basic .NET"));
    }

    private void createProjectTypes() {
        metaDataService.create(new ProjectType("Research Project"));
        metaDataService.create(new ProjectType("Customization"));
        metaDataService.create(new ProjectType("New Development"));
        metaDataService.create(new ProjectType("Product Enhancement"));
        metaDataService.create(new ProjectType("Critical"));
    }

    private void createSoftwareLicenses() {
        metaDataService.create(new SoftwareLicense("Affero GPL"));
        metaDataService.create(new SoftwareLicense("Apache 2.0 License"));
        metaDataService.create(new SoftwareLicense("BSD License"));
        metaDataService.create(new SoftwareLicense("Eclipse Public License"));
        metaDataService.
                create(new SoftwareLicense("GNU General Public License"));
        metaDataService.
                create(new SoftwareLicense("GNU Lesser General Public License"));
        metaDataService.create(new SoftwareLicense("IBM Public License"));
        metaDataService.create(new SoftwareLicense("MIT license / X11 license"));
        metaDataService.create(new SoftwareLicense("Mozilla Public License"));
        metaDataService.create(new SoftwareLicense("Public Domain"));
        metaDataService.create(new SoftwareLicense("Open Software License"));
    }

    private void createSoftwareTypes() {
        metaDataService.
                create(new SoftwareType("API (Application Programming Interface)"));
        metaDataService.create(new SoftwareType("CLI (Command Line Interface)"));
        metaDataService.create(new SoftwareType("Desktop UI (User Interface)"));
        metaDataService.create(new SoftwareType("Web UI (User Interface)"));
        metaDataService.create(new SoftwareType("SOAP Service"));
        metaDataService.create(new SoftwareType("RESTful Service"));
        metaDataService.create(new SoftwareType("Service"));
    }

    private void createSourceCodeManagementTools() {
        metaDataService.create(new SourceCodeManagementTool("Subversion"));
        metaDataService.create(new SourceCodeManagementTool("Git"));
        metaDataService.create(new SourceCodeManagementTool("Mercurial"));
        metaDataService.create(new SourceCodeManagementTool("CVS"));
        metaDataService.create(new SourceCodeManagementTool("SourceSafe"));
        metaDataService.
                create(new SourceCodeManagementTool("Team Foundation Version Control"));
    }

    private void createStaticAnalysisTools() {
        metaDataService.create(new StaticAnalysisTool("Sonar"));
        metaDataService.
                create(new StaticAnalysisTool("Visual Studio Team System"));
        metaDataService.
                create(new StaticAnalysisTool("IBM Rational AppScan Source Edition"));
        metaDataService.create(new StaticAnalysisTool("FindBugs"));
        metaDataService.create(new StaticAnalysisTool("PMD"));
        metaDataService.create(new StaticAnalysisTool("Clang"));
        metaDataService.create(new StaticAnalysisTool("Pylint"));
    }

    private void createTestManagementTools() {
        metaDataService.create(new TestManagementTool("Testlink"));
        metaDataService.
                create(new TestManagementTool("IBM Rational Quality Manager"));
        metaDataService.create(new TestManagementTool("HP Quality Center"));
        metaDataService.create(new TestManagementTool("Meliora Testlab"));
        metaDataService.create(new TestManagementTool("TestComplete"));
    }
    
    private void createTopics() {
        metaDataService.create(new Topic("Automotive"));
        metaDataService.create(new Topic("Banking"));
        metaDataService.create(new Topic("Farming"));
        metaDataService.create(new Topic("Food"));
        metaDataService.create(new Topic("Health"));
        metaDataService.create(new Topic("Military"));
        metaDataService.create(new Topic("Mobile"));
        metaDataService.create(new Topic("Renewable Energies"));
    }
    
    private void createSoftwareDevelopmentMethodologies() {
        metaDataService.create(new SoftwareDevelopmentMethodology("Agile/SCRUM"));
        metaDataService.create(new SoftwareDevelopmentMethodology("Agile/Kanban"));
        metaDataService.create(new SoftwareDevelopmentMethodology("RUP"));
        metaDataService.create(new SoftwareDevelopmentMethodology("Iterative"));
        metaDataService.create(new SoftwareDevelopmentMethodology("Waterfall"));
    }
}
