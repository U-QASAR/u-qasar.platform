package eu.uqasar.util;

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


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.servlet.resource.WebResourceLocator;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequest;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.jboss.weld.context.http.Http;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import eu.uqasar.model.company.Company;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.lifecycle.RupStage;
import eu.uqasar.model.measure.MetricSource;
import eu.uqasar.model.measure.Scale;
import eu.uqasar.model.measure.Unit;
import eu.uqasar.model.meta.ContinuousIntegrationTool;
import eu.uqasar.model.meta.CustomerType;
import eu.uqasar.model.meta.IssueTrackingTool;
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
import eu.uqasar.model.process.Process;
import eu.uqasar.model.product.Product;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.qmtree.QModelStatus;
import eu.uqasar.model.quality.indicator.Domain;
import eu.uqasar.model.quality.indicator.Paradigm;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Version;
import eu.uqasar.model.role.Role;
import eu.uqasar.model.settings.mail.MailSettings;
import eu.uqasar.model.settings.platform.PlatformSettings;
import eu.uqasar.model.settings.qmodel.QModelSettings;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.user.Gender;
import eu.uqasar.model.user.RegistrationStatus;
import eu.uqasar.model.user.Team;
import eu.uqasar.model.user.TeamMembership;
import eu.uqasar.model.user.User;
import eu.uqasar.service.AuthenticationService;
import eu.uqasar.service.PlatformSettingsService;
import eu.uqasar.service.ProcessService;
import eu.uqasar.service.ProductService;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.service.dataadapter.AdapterSettingsService;
import eu.uqasar.service.dataadapter.JenkinsDataService;
import eu.uqasar.service.dataadapter.JiraDataService;
import eu.uqasar.service.dataadapter.SonarDataService;
import eu.uqasar.service.dataadapter.TestLinkDataService;
import eu.uqasar.service.meta.IssueTrackingToolService;
import eu.uqasar.service.meta.MetaDataService;
import eu.uqasar.service.meta.ProgrammingLanguageService;
import eu.uqasar.service.meta.QModelTagDataService;
import eu.uqasar.service.meta.StaticAnalysisToolService;
import eu.uqasar.service.settings.MailSettingsService;
import eu.uqasar.service.settings.QModelSettingsService;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.service.user.TeamMembershipService;
import eu.uqasar.service.user.TeamService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.util.initialize.MetaDataInitializer;
import eu.uqasar.util.rules.RulesTimer;
import eu.uqasar.web.upload.UserProfilePictureUploadHelper;

/**
 *
 * <p>
 */
@Singleton
@Startup
@ApplicationScoped
public class ModelInitializer implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7617782923344156164L;

    @Inject
    @Http
    private ConversationContext context;

    @Inject
    @Bound
    private BoundConversationContext boundContext;

    // inject a logger
    @Inject
    private Logger logger;

    @Inject
    private UserProfilePictureUploadHelper userProfilePictureHelper;

    @Inject
    private EntityManager em;

    @Inject
    private UserService userService;

    @Inject
    private TeamService teamService;

    @Inject
    private TeamMembershipService teamMembershipService;

    @Inject
    private TreeNodeService treeNodeService;

    @Inject
    private QMTreeNodeService qmtreeNodeService;

    @Inject
    private ProductService productService;

    @Inject
    private ProcessService processService;

    @Inject
    private MailSettingsService mailSettingsService;

    @Inject
    private SonarDataService sonarDataService;

    @Inject
    private JiraDataService jiraDataService;

    @Inject
    private TestLinkDataService testLinkDataService;

    @Inject
    private AdapterSettingsService adapterSettingsService;

    @Inject
    private MetaDataInitializer metaDataInitializer;
    
    @Inject
    private RulesTimer rulesTimer;

    @Inject
    @Named(MetaDataService.NAME)
    private MetaDataService metaDataService;
    
    @Inject
    private QModelSettingsService qmodelSettingsService;
    
    @Inject
    private CompanyService companyService;
    
    @Inject    
    private QModelTagDataService qmTagDataService;
    
    @Inject 
    private ProgrammingLanguageService programmingLanguageService;
    
    @Inject
    private StaticAnalysisToolService staticAnalysisToolService;
    
    @Inject
    private IssueTrackingToolService issueTrackingToolService;
    
    @Inject
    private PlatformSettingsService platformSettingsService;
    
    @Inject
    private JenkinsDataService jenkinsDataService;
    
    private static final String ddlKey = "hibernate.hbm2ddl.auto";
    private static final String create = "create";

    /**
     *
     */
    @PostConstruct
    public void populateDB() {
        logger.info("----- POPULATING DB -----");

        BoundRequest storage = null;
        if (!context.isActive() && !boundContext.isActive()) {
            Map<String, Object> session = new HashMap<>();
            Map<String, Object> request = new HashMap<>();
            storage = new MutableBoundRequest(request, session);
            boundContext.associate(storage);
            boundContext.activate();
        }

        try {
            // initialize hibernate full text search
        	// Restrict the batch size in order to remove out of memory issues
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
            fullTextEntityManager.createIndexer().batchSizeToLoadObjects(2).startAndWait();
            
            // check if we need to re-populate the db
            if (!shouldPopulateDB()) {
                // create base data
                createAbsolutelyNecessaryBaseData();
            } else {
                // create initial test data
                createInitialData();
            }
            
            // Initialize the sample rules
            rulesTimer.initRules();
            
            // Initialize timer for fetching data by using the adapters
            adapterSettingsService.initAdapterDataTimer();
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            if (storage != null) {
                boundContext.deactivate();
                boundContext.dissociate(storage);
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        logger.info("----- SHUTTING DOWN -----");
    }

    /**
     * Checks whether we should populate the DB, depending on
     * hibernate.hbm2ddl.auto setting in persistence.xml.
     * <p>
     * @return
     */
    private boolean shouldPopulateDB() {
        Map<String, Object> props = em.getEntityManagerFactory()
                .getProperties();
        if (props.containsKey(ddlKey)) {
            Object ddl = props.get(ddlKey);
            if (ddl instanceof String && ((String) ddl).contains(create)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates base data or nothing at all.
     * <p>
     * @throws Exception
     */
    private void createAbsolutelyNecessaryBaseData() throws Exception {
        // TODO: add your own implementation here.
    }

    private void createInitialData() throws Exception {
        createSettings();
        metaDataInitializer.initialize();
        Company[] companies = createCompanies();
        User[] users = createUsers(companies);
        createTeams(users);
        // TODO: Enable creation of a sample QA project if needed. 
//        createProjects();
        createQModels(companies);
        createProducts();
        createProcesses();
    }

    private void createSettings() {
        createMailSettings();
        createQModelSettings();
        createPlatformSettings();
    }

    // TODO: Read these from a config file
    // In order to use the E-Mail functionality for now, change to use your SMTP server
    private void createMailSettings() {
        MailSettings settings = new MailSettings();
        settings.setHost("");
        settings.setPort(25);
        settings.setAuthenticationRequired(true);
        settings.setTlsRequired(false);
        settings.setAuthUser("");
        settings.setAuthPassword("");
        settings.setFromAddress("noreply@uqasar.eu");
        settings.setFromUser("U-QASAR");

        mailSettingsService.create(settings);
    }

    private void createPlatformSettings() {
    	PlatformSettings setting = new PlatformSettings("adapterdata_update_interval", "1440");
    	platformSettingsService.create(setting);
    	PlatformSettings setting2 = new PlatformSettings("sonar_project", "U-QASAR");
    	platformSettingsService.create(setting2);
    }

    
    private Company[] createCompanies() {
    	int companyCount = 1;
    	Company[] companies = new Company[companyCount];
    	
    	Company testCompany = new Company("Test Company", "TEST");
    	testCompany.setPhone("");
    	testCompany.setFax("");
    	testCompany.setStreet("");
    	testCompany.setZipcode(0);
    	testCompany.setCity("");
    	testCompany.setCountry("");
    	companies[--companyCount] = companyService.create(testCompany);

    	return companies;
    }
    
    
    private User[] createUsers(Company[] companies) {
        int userCount = 1;
        User[] users = new User[userCount];

        User user = prepareUser(new User("Admin", "User", "admin", "admin@example.net"));
        user.setCompany(companies[0]);
		users[--userCount] = userService.create(user);
        
        return users;
    }
    
    private Date getBirthday(int year, int month, int day) {
        LocalDate date = new LocalDate(year, month, day);
        return date.toDate();
    }

    private void placeUserPictureInPictueProfileUploadFolder(User user, final String imageName) {
        try {
            WebResourceLocator locator = new WebResourceLocator();
            final String endPath = "/" + imageName;
            InputStream is = locator.getWebResource(endPath);
            if (is != null) {
				Path newFileName = UserProfilePictureUploadHelper.getNewFileName(user);
                userProfilePictureHelper.processFile(is, newFileName);
            }
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }

    }

    private Team[] createTeams(User[] users) {

        Team[] teams = new Team[1];

        Team team = new Team();
        team.setName("U-QASAR Admin Team");
        team.setDescription("A team for administration of the U-QASAR platform");
        team = teamService.create(team);
        teams[0] = team;

        Set<TeamMembership> memberships = new HashSet<>();
        for (User user : users) {
            TeamMembership membership = new TeamMembership();
            membership.setUser(user);
			membership.setRole(Role.values()[new Random().nextInt(Role.values().length)]);
            membership.setTeam(team);
            membership = teamMembershipService.create(membership);
            memberships.add(membership);
        }

        return teams;
    }

    private User prepareUser(User user, Role role) {
        user.setRole(role);
        user.setRegistrationStatus(RegistrationStatus.CONFIRMED);
        user.setRegistrationDate(new Date());
        user.setGender(Gender.Male);
        user.setSkillCount(new Random().nextInt(100)); 
        
        Set<ProgrammingLanguage> pl = new HashSet<>();
        Set<ContinuousIntegrationTool> ci = new HashSet<>();
        Set<SoftwareLicense> sl = new HashSet<>();
        Set<SoftwareType> st = new HashSet<>();
        Set<ProjectType> pt = new HashSet<>();
        Set<IssueTrackingTool> it = new HashSet<>();
        Set<TestManagementTool> tm = new HashSet<>();
        Set<StaticAnalysisTool> sa = new HashSet<>();
        Set<Topic> topics = new HashSet<>();
        Set<CustomerType> ct = new HashSet<>();
        Set<SourceCodeManagementTool> sm = new HashSet<>();
        Set<SoftwareDevelopmentMethodology> sdvm = new HashSet<>();
        
        pl = getSomeMetaDataForUser(pl, ProgrammingLanguage.class);
        ci = getSomeMetaDataForUser(ci, ContinuousIntegrationTool.class);
        sl = getSomeMetaDataForUser(sl, SoftwareLicense.class);
        st = getSomeMetaDataForUser(st, SoftwareType.class);
        pt = getSomeMetaDataForUser(pt, ProjectType.class);
        it = getSomeMetaDataForUser(it, IssueTrackingTool.class);
        tm = getSomeMetaDataForUser(tm, TestManagementTool.class);
        sa = getSomeMetaDataForUser(sa, StaticAnalysisTool.class);
        topics = getSomeMetaDataForUser(topics, Topic.class);
        ct = getSomeMetaDataForUser(ct, CustomerType.class);
        sm = getSomeMetaDataForUser(sm, SourceCodeManagementTool.class);
        sdvm = getSomeMetaDataForUser(sdvm, SoftwareDevelopmentMethodology.class);
        
        user.setKnownProgrammingLanguages(pl);
        user.setKnownContinuousIntegrationTools(ci);
        user.setKnownIssueTrackingTools(it);
        user.setKnownSourceCodeManagementTools(sm);
        user.setKnownStaticAnalysisTool(sa);
        user.setKnownTestManagementTools(tm);
        user.setCustomerTypes(ct);
        user.setProjectTypes(pt);
        user.setTopics(topics);
        user.setSoftwareLicenses(sl);
        user.setSoftwareTypes(st);
        user.setSoftwareDevelopmentMethodologies(sdvm);
        
        try {
            byte[] salt = AuthenticationService.generateSalt();
            user.setPwSalt(salt);
			user.setPassword(AuthenticationService.getEncryptedPassword(user.getLastName(), salt));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return user;
    }
    
    
    private <T> Set<T> getSomeMetaDataForUser(Set<T> set, Class<T> clazz){
    	
        for(int item = 1; item < 6; item++){
        	 List<T> list = metaDataService.getAll(clazz);
        	 set.add(list.get(new Random().nextInt(list.size())));
        }
    	return set;
    }

    private User prepareUser(User user) {
        return prepareUser(user, Role.Administrator);
    }

    /**
     * This method may be used to initialize a sample QA project. 
     * @return
     * @throws Exception
     */
    private Project[] createProjects() throws Exception {
        Project project1 = new Project("Test Project", "Test");
        project1.setDescription("Sample QA project for U-QASAR platform");
       
        return new Project[]{project1};
    }

    /**
     *
     * @return
     */
    private QModel[] createQModels(Company[] companies) {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.Developer);
        List<Domain> domains = new ArrayList<>();
        domains.add(Domain.Bank);
        domains.add(Domain.Telecommunications);
        domains.add(Domain.Public);

        QModelTagData qmtagSonar = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "Sonar");
        QModelTagData qmtagClang = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "Clang");
        QModelTagData qmtagRegCust = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "Regular Customer");
        QModelTagData qmtagJira = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "JIRA");
        QModelTagData qmtagRed = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "Redmine");
        QModelTagData qmtagBugz = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "Bugzilla");
        QModelTagData qmtagMantis = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "MantisBT");
        QModelTagData qmtagNewDev = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "New Development");
        QModelTagData qmtagBank = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "Banking");
        QModelTagData qmtagCrit = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "Critical");
        QModelTagData qmtagTestlink = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "Testlink");
        QModelTagData qmtagIBMRat = qmTagDataService.getByMetaDataOrCreate(QModelTagData.class, "IBM Rational Quality Manager");
        
        QModel qm1 = new QModel("Quality Model A", "Quality Model A, U-QASAR");
        qm1.setIsActive(QModelStatus.Active);
        qm1.setEdition("1.0");
        qm1.setCompany(companies[0]);
        qm1.setCompanyId(companies[0].getId());
        qm1.setDescription("pre-loaded quality model");
        
        QMQualityObjective qo1 = new QMQualityObjective("Code coverage above 80%", qm1);
        qo1.setTargetAudience(roles);
        // Set Objective "Code coverage above 80%" description
		qo1.setDescription("Average degree to which the source code  is tested by the set of Test Cases designed for the system at all Test Levels (i.e. Unit, Integration and Acceptance)");
        qo1.setLowerLimit((double) 80);
        qo1.setUpperLimit((double) 90);
        qo1.setDomain(domains);
        qo1.setIndicatorPurpose(Purpose.Process);
        qo1.setParadigm(Paradigm.Waterfall);
        qo1.setCompleted(true);
        qo1.getQModelTagData().add(qmtagSonar);
        qo1.getQModelTagData().add(qmtagClang);
        qo1.getQModelTagData().add(qmtagRegCust);
        
        QMQualityIndicator qi1 = new QMQualityIndicator("Unit Test coverage", qo1);
        qi1.setTargetAudience(roles);
		qi1.setDescription("Average degree to which the source code  is tested by the set of Unit Tests. It should be above 60%");
        qi1.setLowerLimit((double) 60);
        qi1.setUpperLimit((double) 100);
        qi1.setIndicatorPurpose(qo1.getIndicatorPurpose());
        qi1.setParadigm(qo1.getParadigm());
        qi1.setLifeCycleStage(LifeCycleStage.Implementation);
        qi1.setCompleted(true);
        qi1.getQModelTagData().add(qmtagSonar);
        qi1.getQModelTagData().add(qmtagClang);
        qi1.getQModelTagData().add(qmtagRegCust);
 
        QMMetric m1 = new QMMetric("Lines covered by Unit Tests", qi1);
        m1.setDescription("Number of lines of code covered by Unit Tests");
        m1.setSource(MetricSource.StaticAnalysis);
        m1.setScale(Scale.Ordinal);
        m1.setUnit(Unit.Loc);
        m1.setLowerLimit((double) 0);
        m1.setUpperLimit((double) 0);
        m1.setTargetValue(0);
        m1.setWeight(1);
        m1.setCompleted(true);
        m1.getQModelTagData().add(qmtagClang);
        m1.getQModelTagData().add(qmtagRegCust);

        QMQualityIndicator qi2 = new QMQualityIndicator("Integration Test coverage", qo1);
        //qi2.setTargetAudience(roles);
		qi2.setDescription("Average degree to which the source code  is tested by the set of Integration Tests.  It should be 100%");
        qi2.setLowerLimit((double) 60);
        qi2.setUpperLimit((double) 100);
        qi2.setIndicatorPurpose(qo1.getIndicatorPurpose());
        qi2.setParadigm(qo1.getParadigm());
        qi2.setLifeCycleStage(LifeCycleStage.Testing);
        qi2.setCompleted(true);
        qi2.getQModelTagData().add(qmtagSonar);
        qi2.getQModelTagData().add(qmtagClang);
        qi2.getQModelTagData().add(qmtagRegCust);

        QMMetric m2 = new QMMetric("Lines covered by Integration Tests", qi2);
		m2.setDescription("Number of lines of code covered by Integration Tests");
        m2.setSource(MetricSource.TestingFramework);
        m2.setScale(Scale.Ordinal);
        m2.setUnit(Unit.Loc);
        m2.setLowerLimit((double) 0);
        m2.setUpperLimit((double) 0);
        m2.setTargetValue(0);
        m2.setWeight(1);
        m2.setCompleted(true);
        m2.getQModelTagData().add(qmtagClang);
        m2.getQModelTagData().add(qmtagRegCust);

        QMQualityIndicator qi3 = new QMQualityIndicator("Acceptance Test coverage", qo1);
        qi3.setTargetAudience(roles);
		qi3.setDescription("Average degree to which the source code  is tested by the set of Acceptance Tests.It should be above 80%");
        qi3.setLowerLimit((double) 50);
        qi3.setUpperLimit((double) 100);
        qi3.setIndicatorPurpose(qo1.getIndicatorPurpose());
        qi3.setParadigm(qo1.getParadigm());
        qi3.setLifeCycleStage(LifeCycleStage.Testing);
        qi3.setCompleted(true);
        qi3.getQModelTagData().add(qmtagSonar);
        qi3.getQModelTagData().add(qmtagClang);
        qi3.getQModelTagData().add(qmtagRegCust);

        QMMetric m3 = new QMMetric("Lines covered by Acceptance Tests", qi3);
        m3.setDescription("Number of lines of code covered by Acceptance Tests");
        m3.setSource(MetricSource.TestingFramework);
        m3.setScale(Scale.Ordinal);
        m3.setUnit(Unit.Test);
        m3.setLowerLimit((double) 0);
        m3.setUpperLimit((double) 0);
        m3.setTargetValue(0);
        m3.setWeight(1);
        m3.setCompleted(true);
        m3.getQModelTagData().add(qmtagSonar);
        m3.getQModelTagData().add(qmtagClang);
        m3.getQModelTagData().add(qmtagRegCust);

        QMMetric m4 = new QMMetric("Lines of code", qi3);
		m4.setDescription("Number of physical lines that contain at least one character which is neither a whitespace or a tabulation or part of a comment.");
        m4.setSource(MetricSource.TestingFramework);
        m4.setScale(Scale.Ordinal);
        m4.setUnit(Unit.Loc);
        m4.setLowerLimit((double) 0);
        m4.setUpperLimit((double) 0);
        m4.setTargetValue(0);
        m4.setWeight(1);
        m4.setCompleted(true);
        m4.getQModelTagData().add(qmtagSonar);
        m4.getQModelTagData().add(qmtagClang);
        m4.getQModelTagData().add(qmtagRegCust);

        QMQualityObjective qo2 = new QMQualityObjective("Minimize Technical Debt", qm1);
		qo2.setDescription("Work that needs to be done before a particular job can be considered complete.");
        qo2.setTargetAudience(roles);
        qo2.setLowerLimit((double) 0);
        qo2.setUpperLimit((double) 70);
        qo2.setDomain(domains);
        qo2.setIndicatorPurpose(Purpose.Product);
        qo2.setVersion(Version.Alfa);
        qo2.setCompleted(true);
        qo2.getQModelTagData().add(qmtagJira);
        qo2.getQModelTagData().add(qmtagRed);
        qo2.getQModelTagData().add(qmtagBugz);
        qo2.getQModelTagData().add(qmtagNewDev);
        
        QMQualityIndicator qi4 = new QMQualityIndicator("Effort needed to fix all issues", qo2);
        qi4.setTargetAudience(roles);
        qi4.setLowerLimit((double) 0);
        qi4.setUpperLimit((double) 90);
        qi4.setIndicatorPurpose(qo2.getIndicatorPurpose());
        qi4.setVersion(qo2.getVersion());
        qi4.setCompleted(true);
        qi4.getQModelTagData().add(qmtagJira);
        qi4.getQModelTagData().add(qmtagRed);
        qi4.getQModelTagData().add(qmtagBugz);
        qi4.getQModelTagData().add(qmtagNewDev);

        QMMetric m5 = new QMMetric("Number of blockers issues", qi4);
		m5.setDescription("Number of issues that blocks the use of the software");
        m5.setSource(MetricSource.IssueTracker);
        m5.setScale(Scale.Ordinal);
        m5.setUnit(Unit.Issue);
        m5.setLowerLimit((double) 0);
        m5.setUpperLimit((double) 0);
        m5.setTargetValue(0);
        m5.setWeight(1);
        m5.setCompleted(true);
        m5.getQModelTagData().add(qmtagJira);
        m5.getQModelTagData().add(qmtagRed);
        m5.getQModelTagData().add(qmtagBugz);
        m5.getQModelTagData().add(qmtagNewDev);

        QMMetric m6 = new QMMetric("Number of critical/major/minor issues", qi4);
		m6.setDescription("Number of issues qualify as critical, major and minor");
        m6.setSource(MetricSource.IssueTracker);
        m6.setScale(Scale.Ordinal);
        m6.setUnit(Unit.Issue);
        m6.setLowerLimit((double) 0);
        m6.setUpperLimit((double) 0);
        m6.setTargetValue(0);
        m6.setWeight(1);
        m6.setCompleted(true);
        m6.getQModelTagData().add(qmtagJira);
        m6.getQModelTagData().add(qmtagRed);
        m6.getQModelTagData().add(qmtagBugz);
        m6.getQModelTagData().add(qmtagNewDev);

        QMQualityObjective qo3 = new QMQualityObjective("High Degree of Code Documentation", qm1);
        qo3.setDescription("Average percentage of commented lines of code");
        qo3.setTargetAudience(roles);
        qo3.setLowerLimit((double) 60);
        qo3.setUpperLimit((double) 100);
        qo3.setDomain(domains);
        qo3.setIndicatorPurpose(Purpose.Process);
        qo3.setParadigm(Paradigm.Rup);
        qo3.setCompleted(true);
        qo3.getQModelTagData().add(qmtagSonar);
        qo3.getQModelTagData().add(qmtagNewDev);

        QMQualityIndicator qi5 = new QMQualityIndicator("Percentage of commented lines of code", qo3);
        qi5.setTargetAudience(roles);
        qi5.setLowerLimit((double) 0);
        qi5.setUpperLimit((double) 100);
        qi5.setIndicatorPurpose(qo3.getIndicatorPurpose());
        qi5.setParadigm(qo3.getParadigm());
        qi5.setRupStage(RupStage.Transition);
        qi5.setCompleted(true);
        qi5.getQModelTagData().add(qmtagSonar);
        qi5.getQModelTagData().add(qmtagNewDev);

        QMMetric m7 = new QMMetric("Number of comment lines", qi5);
        m7.setDescription("Number of comment lines");
        m7.setSource(MetricSource.StaticAnalysis);
        m7.setScale(Scale.Ordinal);
        m7.setUnit(Unit.Loc);
        m7.setLowerLimit((double) 0);
        m7.setUpperLimit((double) 0);
        m7.setTargetValue(0);
        m7.setWeight(1);
        m7.setCompleted(true);
        m7.getQModelTagData().add(qmtagSonar);
        m7.getQModelTagData().add(qmtagNewDev);

        QMMetric m8 = new QMMetric("Lines of code", qi5);
		m8.setDescription("Number of physical lines that contain at least one character which is neither a whitespace or a tabulation or part of a comment.");
        m8.setSource(MetricSource.StaticAnalysis);
        m8.setScale(Scale.Ordinal);
        m8.setUnit(Unit.Loc);
        m8.setLowerLimit((double) 0);
        m8.setUpperLimit((double) 0);
        m8.setTargetValue(0);
        m8.setWeight(1);
        m8.setCompleted(true);
        m8.getQModelTagData().add(qmtagSonar);
        m8.getQModelTagData().add(qmtagNewDev);

        QMQualityObjective qo4 = new QMQualityObjective("Percentage of testing completion", qm1);
        qo4.setTargetAudience(roles);
        qo4.setDescription("Percentage of testing completion");
        qo4.setLowerLimit((double) 80);
        qo4.setUpperLimit((double) 100);
        qo4.setDomain(domains);
        qo4.setIndicatorPurpose(Purpose.Process);
        qo4.setParadigm(Paradigm.Waterfall);
        qo4.setCompleted(true);
        qo4.getQModelTagData().add(qmtagMantis);
        qo4.getQModelTagData().add(qmtagCrit);
        qo4.getQModelTagData().add(qmtagIBMRat);
        qo4.getQModelTagData().add(qmtagTestlink);
        qo4.getQModelTagData().add(qmtagBank);
        qo4.setTargetValue(100);
        
        QMQualityIndicator qi6 = new QMQualityIndicator("Percentage of functional testing completion", qo4);
        qi6.setTargetAudience(roles);
		qi6.setDescription("100 * Executed functional test cases / Planned functional test cases. It should be above 80%");
        qi6.setLowerLimit((double) 80);
        qi6.setUpperLimit((double) 100);
        qi6.setIndicatorPurpose(qo4.getIndicatorPurpose());
        qi6.setParadigm(qo4.getParadigm());
        qi6.setLifeCycleStage(LifeCycleStage.Testing);
        qi6.setCompleted(true);
        qi6.getQModelTagData().add(qmtagMantis);
        qi6.getQModelTagData().add(qmtagCrit);
        qi6.getQModelTagData().add(qmtagIBMRat);
        qi6.getQModelTagData().add(qmtagTestlink);
        qi6.getQModelTagData().add(qmtagBank);
        qi6.setTargetValue(100);
        
        QMMetric m9 = new QMMetric("Number of Passed Tests", qi6);
        m9.setDescription("Number of Passed Tests");
        m9.setSource(MetricSource.TestingFramework);
        m9.setScale(Scale.Ordinal);
        m9.setUnit(Unit.Test);
        m9.setLowerLimit((double) 0);
        m9.setUpperLimit((double) 0);
        m9.setTargetValue(0);
        m9.setWeight(1);
        m9.setCompleted(true);
        m9.getQModelTagData().add(qmtagMantis);
        m9.getQModelTagData().add(qmtagCrit);
        m9.getQModelTagData().add(qmtagIBMRat);
        m9.getQModelTagData().add(qmtagTestlink);
        m9.getQModelTagData().add(qmtagBank);
        
        QMMetric m10 = new QMMetric("Number of Failed Tests", qi6);
        m10.setDescription("Number of Failed Tests");
        m10.setSource(MetricSource.TestingFramework);
        m10.setScale(Scale.Ordinal);
        m10.setUnit(Unit.Test);
        m10.setLowerLimit((double) 0);
        m10.setUpperLimit((double) 0);
        m10.setTargetValue(0);
        m10.setWeight(1);
        m10.setCompleted(true);
        m10.getQModelTagData().add(qmtagMantis);
        m10.getQModelTagData().add(qmtagCrit);
        m10.getQModelTagData().add(qmtagIBMRat);
        m10.getQModelTagData().add(qmtagTestlink);
        m10.getQModelTagData().add(qmtagBank);
        
        QMMetric m11 = new QMMetric("Number of Blocked Tests", qi6);
        m11.setDescription("Number of Blocked Tests");
        m11.setSource(MetricSource.TestingFramework);
        m11.setScale(Scale.Ordinal);
        m11.setUnit(Unit.Test);
        m11.setLowerLimit((double) 0);
        m11.setUpperLimit((double) 0);
        m11.setTargetValue(0);
        m11.setWeight(1);
        m11.setCompleted(true);
        m11.getQModelTagData().add(qmtagMantis);
        m11.getQModelTagData().add(qmtagCrit);
        m11.getQModelTagData().add(qmtagIBMRat);
        m11.getQModelTagData().add(qmtagTestlink);
        m11.getQModelTagData().add(qmtagBank);
        
        QMMetric m12 = new QMMetric("Number of Not Run Tests", qi6);
        m12.setDescription("Number of Not Run Tests");
        m12.setSource(MetricSource.TestingFramework);
        m12.setScale(Scale.Ordinal);
        m12.setUnit(Unit.Test);
        m12.setLowerLimit((double) 0);
        m12.setUpperLimit((double) 0);
        m12.setTargetValue(0);
        m12.setWeight(1);
        m12.setCompleted(true);
        m12.getQModelTagData().add(qmtagMantis);
        m12.getQModelTagData().add(qmtagCrit);
        m12.getQModelTagData().add(qmtagIBMRat);
        m12.getQModelTagData().add(qmtagTestlink);
        m12.getQModelTagData().add(qmtagBank);
        
        QMMetric m13 = new QMMetric("Total Tests", qi6);
        m13.setDescription("Total Tests");
        m13.setSource(MetricSource.TestingFramework);
        m13.setScale(Scale.Ordinal);
        m13.setUnit(Unit.Test);
        m13.setLowerLimit((double) 0);
        m13.setUpperLimit((double) 0);
        m13.setTargetValue(0);
        m13.setWeight(1);
        m13.setCompleted(true);
        m13.getQModelTagData().add(qmtagMantis);
        m13.getQModelTagData().add(qmtagCrit);
        m13.getQModelTagData().add(qmtagIBMRat);
        m13.getQModelTagData().add(qmtagTestlink);
        m13.getQModelTagData().add(qmtagBank);
        
		qm1.setCompleted(true);
        qm1 = (QModel) qmtreeNodeService.create(qm1);

        return new QModel[]{qm1};
    }

    /**
     *
     * @return
     */
    private Product[] createProducts() {
        Product product1 = new Product("U-QASAR Test Product");
		product1.setDescription("Test product");
        product1.setVersion("0.1");
        int monthOffset = 6;
        int day = LocalDate.now().plusMonths(monthOffset).dayOfMonth().getMaximumValue();
        product1.setReleaseDate(DateTime.now().plusMonths(monthOffset).withDayOfMonth(day).toDate());
        product1 = productService.create(product1);

        return new Product[]{product1};
    }

    /**
     *
     * @return
     */
    private Process[] createProcesses() {
        Process process1 = new Process("U-QASAR Development");
        process1.setDescription("Development of the U-QASAR platform");
        process1.setStartDate(DateTime.now().minusDays(14).toDate());
        process1.setEndDate(DateTime.now().plusMonths(5).toDate());
        process1 = processService.create(process1);

        return new Process[]{process1};
    }
    

    private void createQModelSettings() {
        QModelSettings settings = new QModelSettings();

        settings.setHigh("QO");
        settings.setMedium("QI");
        settings.setLow("QM");
        settings.setHighEntity("Quality Objective");
        settings.setMediumEntity("Quality Indicator");
        settings.setLowEntity("Quality Metric");
      
        qmodelSettingsService.create(settings);
    }
}
