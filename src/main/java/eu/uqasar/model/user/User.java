package eu.uqasar.model.user;

import static eu.uqasar.model.user.UserSource.LDAP;
import static javax.persistence.CascadeType.MERGE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.joda.time.Years;

import ro.fortsoft.wicket.dashboard.Dashboard;
import eu.uqasar.message.UQasarMessage;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.Namable;
import eu.uqasar.model.company.Company;
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
import eu.uqasar.model.role.Role;
import eu.uqasar.model.tree.Project;
import eu.uqasar.web.components.resources.UserPictureResource;

@Entity(name = "User")
@XmlRootElement
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")

@Indexed
public class User extends AbstractEntity implements Namable {

    /**
     *
     */
    private static final long serialVersionUID = -9178525827563264107L;

    @NotNull
    @Size(min = 2)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
    private String firstName;

    @NotNull
    @Size(min = 2)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
    private String lastName;

    @NotNull
    @Column(unique = true)
    @Size(min = 3, max = 96)
    @Pattern(regexp = "[A-Za-z0-9]*")
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
    private String userName;

    @Email
    @NotNull
    @Column(unique = true)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
    private String mail;

    private byte[] password;
    private byte[] pwSalt;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate = new Date();
    
    @NotNull
    private int skillCount = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;

    private String registrationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date resetPWRequestDate;

    private String resetPWRequestToken;

    private String preferredLocaleString;

    private UQasarMessage.MessageType preferredMessageType = UQasarMessage.MessageType.HTML;

    @Enumerated(EnumType.STRING)
    private UserSource source = UserSource.UQASAR;

    @Enumerated(EnumType.STRING)
    private Role userRole = Role.User;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Temporal(TemporalType.DATE)
    private Date birthDay;

    @ElementCollection
    @Lob
    private List<Dashboard> dashboards = new ArrayList<Dashboard>();
    
    @ManyToMany(cascade = MERGE)
	private Set<CustomerType> customerTypes = new HashSet<>();
	@ManyToMany(cascade = MERGE)
	private Set<ProjectType> projectTypes = new HashSet<>();
	@ManyToMany(cascade = MERGE)
	private Set<SoftwareType> softwareTypes = new HashSet<>();
	@ManyToMany(cascade = MERGE)
	private Set<SoftwareLicense> softwareLicenses = new HashSet<>();
	@ManyToMany(cascade = MERGE)
	private Set<SoftwareDevelopmentMethodology> softwareDevelopmentMethodologies = new HashSet<>();
	@ManyToMany(cascade = MERGE)
	private Set<Topic> topics = new HashSet<>();

	

	public Set<CustomerType> getCustomerTypes() {
		return customerTypes;
	}

	public void setCustomerTypes(Set<CustomerType> customerTypes) {
		this.customerTypes = customerTypes;
	}

	public Set<ProjectType> getProjectTypes() {
		return projectTypes;
	}

	public void setProjectTypes(Set<ProjectType> projectTypes) {
		this.projectTypes = projectTypes;
	}

	public Set<SoftwareType> getSoftwareTypes() {
		return softwareTypes;
	}

	public void setSoftwareTypes(Set<SoftwareType> softwareTypes) {
		this.softwareTypes = softwareTypes;
	}

	public Set<SoftwareLicense> getSoftwareLicenses() {
		return softwareLicenses;
	}

	public void setSoftwareLicenses(Set<SoftwareLicense> softwareLicenses) {
		this.softwareLicenses = softwareLicenses;
	}

	public Set<SoftwareDevelopmentMethodology> getSoftwareDevelopmentMethodologies() {
		return softwareDevelopmentMethodologies;
	}

	public void setSoftwareDevelopmentMethodologies(
			Set<SoftwareDevelopmentMethodology> softwareDevelopmentMethodologies) {
		this.softwareDevelopmentMethodologies = softwareDevelopmentMethodologies;
	}

	public Set<Topic> getTopics() {
		return topics;
	}

	public void setTopics(Set<Topic> topics) {
		this.topics = topics;
	}

	@ManyToOne
	private Company company;

	
	
	public Company getCompany() {
		return company;
	}

	public User setCompany(Company company) {
		this.company = company;
        return this;
	}

	@ManyToMany(cascade = MERGE)
    private Set<ProgrammingLanguage> knownProgrammingLanguages = new HashSet<>();

    @ManyToMany(cascade = MERGE)
    private Set<IssueTrackingTool> knownIssueTrackingTools = new HashSet<>();

    @ManyToMany(cascade = MERGE)
    private Set<SourceCodeManagementTool> knownSourceCodeManagementTools = new HashSet<>();

    @ManyToMany(cascade = MERGE)
    private Set<StaticAnalysisTool> knownStaticAnalysisTools = new HashSet<>();

    @ManyToMany(cascade = MERGE)
    private Set<TestManagementTool> knownTestManagementTools = new HashSet<>();

    @ManyToMany(cascade = MERGE)
    private Set<ContinuousIntegrationTool> knownContinuousIntegrationTools = new HashSet<>();

    public Set<ContinuousIntegrationTool> getKnownContinuousIntegrationTools() {
        return knownContinuousIntegrationTools;
    }

    public User setKnownContinuousIntegrationTools(Set<ContinuousIntegrationTool> knownContinuousIntegrationTools) {
        this.knownContinuousIntegrationTools = knownContinuousIntegrationTools;
        return this;
    }
    
    public Set<ProgrammingLanguage> getKnownProgrammingLanguages() {
        return knownProgrammingLanguages;
    }

    public User setKnownProgrammingLanguages(Set<ProgrammingLanguage> knownProgrammingLanguages) {
        this.knownProgrammingLanguages = knownProgrammingLanguages;
        return this;
    }

    public Set<IssueTrackingTool> getKnownIssueTrackingTools() {
        return knownIssueTrackingTools;
    }

    public User setKnownIssueTrackingTools(Set<IssueTrackingTool> knownIssueTrackingTools) {
        this.knownIssueTrackingTools = knownIssueTrackingTools;
        return this;
    }

    public Set<SourceCodeManagementTool> getKnownSourceCodeManagementTools() {
        return knownSourceCodeManagementTools;
    }

    public User setKnownSourceCodeManagementTools(Set<SourceCodeManagementTool> knownSourceCodeManagementTools) {
        this.knownSourceCodeManagementTools = knownSourceCodeManagementTools;
        return this;
    }

    public Set<StaticAnalysisTool> getKnownStaticAnalysisTools() {
        return knownStaticAnalysisTools;
    }

    public User setKnownStaticAnalysisTool(Set<StaticAnalysisTool> knownStaticAnalysisTools) {
        this.knownStaticAnalysisTools = knownStaticAnalysisTools;
        return this;
    }

    public Set<TestManagementTool> getKnownTestManagementTools() {
        return knownTestManagementTools;
    }

    public User setKnownTestManagementTools(Set<TestManagementTool> knownTestManagementTools) {
        this.knownTestManagementTools = knownTestManagementTools;
        return this;
    }

    @Override
    public String getUniqueName() {
        return mail;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public User setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
        return this;
    }

    public Integer getAge() {
        if (this.birthDay != null) {
            DateTime start = new DateTime(this.birthDay);
            DateTime end = DateTime.now();
            return Years.yearsBetween(start.toLocalDate(), end.toLocalDate()).
                    getYears();
        }
        return null;
    }

    public Gender getGender() {
        return gender;
    }

    public User setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Role getRole() {
        return userRole;
    }

    public User setRole(Role role) {
        this.userRole = role;
        return this;
    }

    public UQasarMessage.MessageType getPreferredMessageType() {
        return preferredMessageType;
    }

    public User setPreferredMessageType(UQasarMessage.MessageType type) {
        this.preferredMessageType = type;
        return this;
    }

    public Locale getPreferredLocale() {
        if (preferredLocaleString == null) {
            return null;
        } else {
            return new Locale(preferredLocaleString);
        }
    }

    public User setPreferredLocale(Locale locale) {
        this.preferredLocaleString = locale.getLanguage();
        return this;
    }

    public User() {
        this(null, null, null, null);
    }

    public User(String firstname, String lastname) {
        this(firstname, lastname, null, null);
    }

    public User(String firstname, String lastname, String alias) {
        this(firstname, lastname, alias, null);
    }

    public User(String firstname, String lastname, String userName,
            String mail) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.mail = mail;
        this.userName = userName;
    }

    @JsonIgnore
    @XmlTransient
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    @JsonIgnore
    @XmlTransient
    public String getFullNameWithUserName() {
        return String.format("%s %s (%s)", firstName, lastName, userName);
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @JsonIgnore
    @XmlTransient
    public byte[] getPassword() {
        return password;
    }

    public User setPassword(byte[] password) {
   		this.password = password.clone(); 
        return this;
    }

    @JsonIgnore
    @XmlTransient
    public byte[] getPwSalt() {
        return pwSalt;
    }

    public User setPwSalt(byte[] pwSalt) {
   		this.pwSalt = pwSalt.clone();
    	return this;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public User setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public User setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
        return this;
    }

    @JsonIgnore
    @XmlTransient
    public String getRegistrationToken() {
        return registrationToken;
    }

    public User setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
        return this;
    }

    public int getSkillCount() {
		return skillCount;
	}

	public void setSkillCount(int skillCount) {
		this.skillCount = skillCount;
	}
	
	public void incrementSkillCount(){
		this.skillCount++;
	}

	public Date getResetPWRequestDate() {
        return resetPWRequestDate;
    }

    public User setResetPWRequestDate(Date resetPWRequestDate) {
        this.resetPWRequestDate = resetPWRequestDate;
        return this;
    }

    @JsonIgnore
    @XmlTransient
    public String getResetPWRequestToken() {
        return resetPWRequestToken;
    }

    public User setResetPWRequestToken(String resetPWRequestToken) {
        this.resetPWRequestToken = resetPWRequestToken;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getMail() {
        return mail;
    }

    public User setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public UserSource getSource() {
        return source;
    }

    public User setSource(UserSource source) {
        this.source = source;
        return this;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    public boolean hasAnyRoles(Role... roles) {
        for (Role role : roles) {
            if (role == this.userRole) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    @XmlTransient
    public boolean matchesInputFilter(final String filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }
        if (this.getFullNameWithUserName() == null || this.
                getFullNameWithUserName().isEmpty()) {
            return false;
        }
        return this.getFullNameWithUserName().toLowerCase().contains(filter.
                toLowerCase());
    }

    public List<Dashboard> getDashboards() {
        return dashboards;
    }

    public User setDashboards(List<Dashboard> dashboards) {
        this.dashboards = dashboards;
        return this;
    }

    public User addDashboard(Dashboard dashboard) {
        // Add only if not exists
        if (!getDashboards().contains(dashboard)) {
            getDashboards().add(dashboard);
        }
        return this;
    }

    @JsonIgnore
    @XmlTransient
    public static ContextRelativeResource getAnonymousPicture() {
        return UserPictureResource.defaultProfilePictureRef;
    }

    @JsonIgnore
    @XmlTransient
    public UserPictureResource getProfilePicture(PictureDimensions dimension) {
        return new UserPictureResource(this, dimension.getMaximumDimension());
    }

    @JsonIgnore
    @XmlTransient
    public ResourceReference getProfilePictureReference() {
        return UserPictureResource.asReference();
    }

    @JsonIgnore
    @XmlTransient
    public PageParameters getProfilePicturePageParameters(PictureDimensions dimension) {
        return new PageParameters().add("userId", this.getId()).
                add("dim", dimension.getMaximumDimension());
    }

    @JsonIgnore
    @XmlTransient
    public PageParameters getUncachedProfilePicturePageParameters(PictureDimensions dimension) {
        return new PageParameters().add("userId", this.getId()).
                add("dim", dimension.getMaximumDimension()).
                add("wicket:anticache", System.currentTimeMillis());
    }

    public boolean isLdapBased() {
        return getSource() == LDAP;
    }

    public enum PictureDimensions {

        Small(64),
        Medium(196),
        Badge(128),
        Large(384),
        NavbarMenu(36),;
        private final int max;

        PictureDimensions(int max) {
            this.max = max;
        }

        public int getMaximumDimension() {
            return this.max;
        }

    }

}
