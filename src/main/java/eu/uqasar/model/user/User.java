package eu.uqasar.model.user;

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


import static eu.uqasar.model.user.UserSource.LDAP;
import static javax.persistence.CascadeType.MERGE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;
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
import eu.uqasar.web.components.resources.UserPictureResource;

@Setter
@Getter
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
    private List<Dashboard> dashboards = new ArrayList<>();
    
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


    @ManyToOne
	private Company company;


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

    public User setKnownStaticAnalysisTool(Set<StaticAnalysisTool> knownStaticAnalysisTools) {
        this.knownStaticAnalysisTools = knownStaticAnalysisTools;
        return this;
    }

    @Override
    public String getUniqueName() {
        return mail;
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

    public Role getRole() {
        return userRole;
    }

    public User setRole(Role role) {
        this.userRole = role;
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

    public void incrementSkillCount(){
		this.skillCount++;
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
