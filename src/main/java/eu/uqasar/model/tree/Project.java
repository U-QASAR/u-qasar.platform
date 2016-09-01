package eu.uqasar.model.tree;

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


import static javax.persistence.CascadeType.MERGE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.joda.time.DateTime;
import org.joda.time.Days;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.analytic.Analysis;
import eu.uqasar.model.company.Company;
import eu.uqasar.model.formula.Formula;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.measure.JiraMetricMeasurement;
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
import eu.uqasar.model.process.Process;
import eu.uqasar.model.product.Product;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.settings.adapter.AdapterSettings;
import eu.uqasar.model.tree.historic.HistoricValuesProject;
import eu.uqasar.model.tree.historic.Snapshot;
import eu.uqasar.model.user.Team;
import eu.uqasar.util.UQasarUtil;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "shortName", "description", "endDate",
		"formulaAverage","formulaToView", "lifeCycleStage", "startDate", "threshold",
		"value", "children"})
@Table(name="project")
@Indexed
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
public class Project extends TreeNode implements Comparable<Project> {

	private static final long serialVersionUID = 1724244299796140695L;
	
	private static final IconType ICON = new IconType("sitemap");
	
	@XmlSchemaType(name = "date")
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date startDate = DateTime.now().toDate();
	
	@XmlSchemaType(name = "date")
	@Temporal(javax.persistence.TemporalType.DATE)
	private Date endDate = DateTime.now().plusDays(730).toDate();
	
	@XmlTransient
	@ManyToOne
	private QModel qmodel;
	
	@XmlTransient
	@ManyToOne
	private Process process;
	
	@XmlTransient
	@ManyToOne
	private Company company;

	@XmlTransient
	@ManyToOne
	private Product product;
	
	@Lob
	private String formulaToView;
	
	@XmlTransient
	private String formulaToEval;
	
	@XmlTransient
	private Double value;
	
	@XmlTransient
    private Date lastUpdated = DateTime.now().toDate();
	
	@OneToOne(cascade = CascadeType.ALL)
	private Threshold threshold = new Threshold();
	
	private boolean formulaAverage = true;
	
	@XmlTransient
	@OneToMany(cascade = CascadeType.ALL)
	private List<HistoricValuesProject> historicValues = new ArrayList<>();
	
	@JsonIgnore
	@XmlTransient
	@OneToMany(cascade = CascadeType.ALL )
	private List<Snapshot > snapshot = new ArrayList<>();
	
	@XmlTransient
	@OneToMany(cascade = CascadeType.ALL, mappedBy="project", orphanRemoval=true)
	private List<AdapterSettings> adapterSettings = new ArrayList<>();
    
	@XmlTransient
	@OneToMany(fetch = FetchType.EAGER,mappedBy = "project", orphanRemoval=true)
    private Set<JiraMetricMeasurement> jirameasurements = new HashSet<>();
   
	@XmlTransient        
    @IndexedEmbedded(prefix = "customerTypes_")
	@ManyToMany(cascade = MERGE)
	private Set<CustomerType> customerTypes = new HashSet<>();
    
	@XmlTransient
	@IndexedEmbedded(prefix = "projectTypes_")
	@ManyToMany(cascade = MERGE)
	private Set<ProjectType> projectTypes = new HashSet<>();
    
	@XmlTransient
	@IndexedEmbedded(prefix = "softwareTypes_")
	@ManyToMany(cascade = MERGE)
	private Set<SoftwareType> softwareTypes = new HashSet<>();
    
	@XmlTransient
	@IndexedEmbedded(prefix = "softwareLicenses_")
	@ManyToMany(cascade = MERGE)
	private Set<SoftwareLicense> softwareLicenses = new HashSet<>();
    
	@XmlTransient
	@IndexedEmbedded(prefix = "topics_")
	@ManyToMany(cascade = MERGE)
	private Set<Topic> topics = new HashSet<>();
    
	@XmlTransient
	@IndexedEmbedded(prefix = "programmingLanguages_")
	@ManyToMany(cascade = MERGE)
    private Set<ProgrammingLanguage> programmingLanguages = new HashSet<>();
    
    @XmlTransient
	@IndexedEmbedded(prefix = "issueTrackingTools_")
    @ManyToMany(cascade = MERGE)
    private Set<IssueTrackingTool> issueTrackingTools = new HashSet<>();
    
    @XmlTransient
	@IndexedEmbedded(prefix = "sourceCodeManagementTools_")
    @ManyToMany(cascade = MERGE)
    private Set<SourceCodeManagementTool> sourceCodeManagementTools = new HashSet<>();
    
    @XmlTransient
	@IndexedEmbedded(prefix = "staticAnalysisTools_")
    @ManyToMany(cascade = MERGE)
    private Set<StaticAnalysisTool> staticAnalysisTools = new HashSet<>();
    
    @XmlTransient
	@IndexedEmbedded(prefix = "testManagementTools_")
    @ManyToMany(cascade = MERGE)
    private Set<TestManagementTool> testManagementTools = new HashSet<>();
    
    @XmlTransient
	@IndexedEmbedded(prefix = "continuousIntegrationTools_")
    @ManyToMany(cascade = MERGE)
    private Set<ContinuousIntegrationTool> continuousIntegrationTools = new HashSet<>();
    
    @XmlTransient
	@IndexedEmbedded(prefix = "softwareDevelopmentMethodologies_")
    @ManyToMany(cascade = MERGE)
    private Set<SoftwareDevelopmentMethodology> softwareDevelopmentMethodologies = new HashSet<>();

    @XmlTransient
	@ManyToMany
    private List<Team> teams = new ArrayList<>();
    
    @XmlTransient
	@OneToMany(cascade = CascadeType.ALL, mappedBy="project")
	private List<Analysis> analysis = new ArrayList<>();

	@XmlTransient
	 // Computed value for similarity when compared to another projects (similarity based on tags and semantic content)
    @Transient
    private Integer similarityValue = 0; 
	
    
    @JsonCreator
	public Project(
			@JsonProperty("description") final String description,
			@JsonProperty("name") final String name,
			@JsonProperty("endDate") final Date endDate,
			@JsonProperty("formulaAverage") final boolean formulaAverage,
			@JsonProperty("formulaToView") final String formulaToView,
			@JsonProperty("shortName") final String key,
			@JsonProperty("lifeCycleStage") final LifeCycleStage lifeCycleStage,
			@JsonProperty("threshold") final Threshold threshold,
			@JsonProperty("startDate") final Date startDate,
			@JsonProperty("value") final Double value,
			@JsonProperty("children") final List<QualityObjective> children) {
			
		
		this.setDescription(description);
		this.setEndDate(endDate);
		this.setFormulaAverage(formulaAverage);
		this.setFormulaToView(formulaToView);
		this.setName(name);
		this.setShortName(key);
		this.setLifeCycleStage(lifeCycleStage);
		this.setThreshold(threshold);
		this.setStartDate(startDate);
		this.setValue(value);
		
		Iterator<QualityObjective> it = children.iterator();
		List<TreeNode> nodes = new LinkedList<>();
		while (it.hasNext()){
			QualityObjective qo = it.next();
			nodes.add(qo);
		}
		this.setChildren(nodes);
	}
    
	public Project() {
	}

	public Project(final String name, final String mKey) {
		this.name = name;
		this.nodeKey = mKey;
	}

	@JsonIgnore	
	public Company getCompany() {
		return company;
	}

	@JsonIgnore
	public void setCompany(Company company) {
		this.company = company;
	}
    public String getDescription() {
		return description;
	}

    @JsonIgnore
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Team> getTeams() {
		return teams;
	}

	@JsonIgnore
	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	@JsonIgnore
	public String getAbbreviatedName(int maxLength) {
		return StringUtils.abbreviate(getName(), maxLength);
	}

	@JsonIgnore
	public String getAbbreviatedName() {
		return getAbbreviatedName(48);
	}

	@Override
	public IconType getIconType() {
		return ICON;
	}

	public Date getStartDate() {
		return startDate;
	}

	@JsonIgnore
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@JsonIgnore
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@JsonIgnore
	public boolean isRunning() {
		return getRemainingDays() > 0;
	}

	@JsonIgnore
	public String getElapsedVsRemainingString() {
		return String.format("%s/%s", getElapsedDays(), getRemainingDays());
	}

	@JsonIgnore
	public String getElapsedVsOverallString() {
		return String.format("%s/%s", getElapsedDays(), getDurationInDays());
	}
	
	@JsonIgnore
	public String getElapsedVsRemainingWithPercentageString() {
		return String.format("%s/%s (%d %%)", getElapsedDays(),
				getRemainingDays(), getDateProgressInPercent());
	}

	@JsonIgnore
	public int getDurationInDays() {
		DateTime end = new DateTime(getEndDate());
		DateTime start = new DateTime(getStartDate());
        return Days.daysBetween(start, end).getDays();
	}

	@JsonIgnore
	public int getElapsedDays() {
		DateTime start = new DateTime(getStartDate());
		int elapsed = Days.daysBetween(start, DateTime.now()).getDays();
		int daysTotal = getDurationInDays();
		return Math.min(elapsed, daysTotal);
	}

	@JsonIgnore
	public int getRemainingDays() {
		DateTime end = new DateTime(getEndDate());
		int remainingDays = Days.daysBetween(DateTime.now(), end).getDays();
		return Math.max(0, remainingDays);
	}

	@JsonIgnore
	public Integer getDateProgressInPercent() {
		float elapsedDays = getElapsedDays();
		float totalDays = getDurationInDays();
		if (elapsedDays <= 0) {
			return 0;
		}
		if (elapsedDays > totalDays) {
			return 100;
		}
		return Float.valueOf((elapsedDays / totalDays) * 100f).intValue();
	}
	
	@JsonIgnore
	@Override
	public Class<QualityObjective> getChildType() {
		return QualityObjective.class;
	}
	
	/**
	 * @return the qmodel
	 */
	@JsonIgnore
	public QModel getQmodel() {
		return qmodel;
	}

	/**
	 * @param qmodel the qmodel to set
	 */
	@JsonIgnore
	public void setQmodel(QModel qmodel) {
		this.qmodel = qmodel;
	}

	/**
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * @param process the process to set
	 */
	@JsonIgnore
	public void setProcess(Process process) {
		this.process = process;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	@JsonIgnore
	public void setProduct(final Product product) {
		this.product = product;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	@XmlElement(name="value")
	@JsonIgnore
	public void setValue(final Double value) {
		this.value = value;
		
		// Update the quality status
		this.setQualityStatus(QualityStatus.getQualityStatusForValue(value, threshold));
	}
	
	/**
	 * @return
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated
	 */
	@JsonIgnore
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	/**
	 * @return the threshold
	 */
	public Threshold getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(final Threshold threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return a List with all the historic values
	 */
	public List<HistoricValuesProject> getHistoricValues() {
		Collections.sort(this.historicValues, Collections.reverseOrder());
		return historicValues;
	}

	/**
	 * @param date
	 * @return 
	 */
	public double getHistoricValue(Date date) {
		for (final HistoricValuesProject h : historicValues) {
			if (UQasarUtil.isDateEqual(h.getDate(), date)) {
				return h.getValue();
			}
		}
		return 0;
	}
	
	/**
	 * @param date
	 * @return the object that persisting date match with the provided date
	 */
	public HistoricValuesProject getHistoricObject(Date date) {
		for (final HistoricValuesProject h : historicValues) {
			if (UQasarUtil.isDateEqual(h.getDate(), date)) {
				return h;
			}
		}
		return null;
	}
	
	
	/**
	 * @param historicValues
	 */
	@JsonIgnore
	public void setHistoricValues(List<HistoricValuesProject> historicValues) {
		this.historicValues = historicValues;
	}

	/**
	 * @param historicValues
	 */
    private void addHistoricValues(final HistoricValuesProject historicValues){
		this.historicValues.add(historicValues);
	}
	
	/**
     */
    private void addHistoricValues(final Project project) {
		addHistoricValues(new HistoricValuesProject(project));
	}
	
	/**
	 * 
	 */
	public void addHistoricValue() {
		addHistoricValues(this);
	}
	
	/**
	 * @return the snapshot
	 */
	public List<Snapshot> getSnapshot() {
		return snapshot;
	}

	/**
	 * @param snapshot the snapshot to set
	 */
	public void setSnapshot(List<Snapshot> snapshot) {
		this.snapshot = snapshot;
	}

	/**
	 * @param snapshot adds the snapshot to the list
	 */
	public void addSnapshot(Snapshot snapshot){
		this.snapshot.add(snapshot);
	}
	
	/**
	 * @param object
	 */
	public void addSnapshot(String object) {
		Snapshot s = new Snapshot(object);
		s.setProject(this);
		s.setDate(new Date());
		s.setLastUpdate(lastUpdated);
		
		this.snapshot.add(s);
	}
	
	/**
	 * @return the number of registered historical values
	 */
	public Integer getNoOfHistoricValues() {
		return this.historicValues.size();
	}

	/**
	 * @return true to use Objective weights to compute Project Quality
	 */
	public boolean isFormulaAverage() {
		return formulaAverage;
	}

	/**
     */
	@JsonIgnore
	public void setFormulaAverage(final boolean formulaAverage) {
		this.formulaAverage = formulaAverage;
	}

	/**
	 * @return the formulaToView
	 */
	public String getFormulaToView() {
		return formulaToView;
	}

	/**
	 * @param formulaToView the formulaToView to set
	 */
	@JsonIgnore
	public void setFormulaToView(final String formulaToView) {
		this.formulaToView = formulaToView;
	}

	/**
	 * @return the formulaToEval
	 */
	public String getFormulaToEval() {
		return formulaToEval;
	}

	/**
	 * @param formulaToEval the formulaToEval to set
	 */
	@JsonIgnore
	public void setFormulaToEval(final String formulaToEval) {
		this.formulaToEval = formulaToEval;
		if (formulaToEval != null && !formulaToEval.isEmpty()) {
			Double val = null;
			Float evaluatedVal = Formula.evalFormula(formulaToEval);
			if (evaluatedVal != null) {
				val = evaluatedVal.doubleValue();
				this.setValue(val);
			}
		}		
	}
	
	/**
	 * Sets the default calculation formulas; 
	 * as of default an average based on the QO values is computed. 
	 */
	@JsonIgnore
	public void setDefaultFormulas() {
		List<TreeNode> nodes = this.getChildren();
		String viewFormula = "";
		String evalFormula = "";
		// Create a sum formula
		evalFormula += "(";
		viewFormula += "(";
        for (TreeNode node : nodes) {
            if (node instanceof QualityObjective) {
                QualityObjective obj = (QualityObjective) node;
                evalFormula += obj.getValue();
                viewFormula += obj.getName();
            }
            evalFormula += "+";
            viewFormula += "+";
        }

		if (evalFormula.endsWith("+")) {
			evalFormula = evalFormula.substring(0, evalFormula.length() - 1);
		}
		if (viewFormula.endsWith("+")) {
			viewFormula = viewFormula.substring(0, viewFormula.length() - 1);
		}
		// Add parentheses
		evalFormula += ")";
		viewFormula += ")";
		// Create the average computation formula
		evalFormula += "/" +nodes.size();
		viewFormula += "/" +nodes.size();
		
		this.setFormulaToEval(evalFormula);
		this.setFormulaToView(viewFormula);
	}
	
	/**
	 * @return the adapterSettings
	 */
	public List<AdapterSettings> getAdapterSettings() {
		return adapterSettings;
	}

	/**
	 * @param adapterSettings the adapterSettings to set
	 */
	@JsonIgnore
	public void setAdapterSettings(List<AdapterSettings> adapterSettings) {
		this.adapterSettings = adapterSettings;
	}
	
	public void addAdapterSettings(AdapterSettings adapterSetting) {
		this.adapterSettings.add(adapterSetting);
	}

	public Set<ProgrammingLanguage> getProgrammingLanguages() {
		return programmingLanguages;
	}

	@JsonIgnore
	public void setProgrammingLanguages(Set<ProgrammingLanguage> programmingLanguages) {
		this.programmingLanguages = programmingLanguages;
	}

	public Set<IssueTrackingTool> getIssueTrackingTools() {
		return issueTrackingTools;
	}

	@JsonIgnore
	public void setIssueTrackingTools(Set<IssueTrackingTool> issueTrackingTools) {
		this.issueTrackingTools = issueTrackingTools;
	}

	public Set<SourceCodeManagementTool> getSourceCodeManagementTools() {
		return sourceCodeManagementTools;
	}

	@JsonIgnore
	public void setSourceCodeManagementTools(
			Set<SourceCodeManagementTool> sourceCodeManagementTools) {
		this.sourceCodeManagementTools = sourceCodeManagementTools;
	}

	public Set<StaticAnalysisTool> getStaticAnalysisTools() {
		return staticAnalysisTools;
	}

	@JsonIgnore
	public void setStaticAnalysisTools(Set<StaticAnalysisTool> staticAnalysisTools) {
		this.staticAnalysisTools = staticAnalysisTools;
	}

	public Set<TestManagementTool> getTestManagementTools() {
		return testManagementTools;
	}

	@JsonIgnore
	public void setTestManagementTools(Set<TestManagementTool> testManagementTools) {
		this.testManagementTools = testManagementTools;
	}

	public Set<ContinuousIntegrationTool> getContinuousIntegrationTools() {
		return continuousIntegrationTools;
	}

	@JsonIgnore
	public void setContinuousIntegrationTools(
			Set<ContinuousIntegrationTool> continuousIntegrationTools) {
		this.continuousIntegrationTools = continuousIntegrationTools;
	}

	public Set<CustomerType> getCustomerTypes() {
		return customerTypes;
	}

	@JsonIgnore
	public void setCustomerTypes(Set<CustomerType> customerTypes) {
		this.customerTypes = customerTypes;
	}

	public Set<ProjectType> getProjectTypes() {
		return projectTypes;
	}

	@JsonIgnore
	public void setProjectTypes(Set<ProjectType> projectTypes) {
		this.projectTypes = projectTypes;
	}

	public Set<SoftwareType> getSoftwareTypes() {
		return softwareTypes;
	}

	@JsonIgnore
	public void setSoftwareTypes(Set<SoftwareType> softwareTypes) {
		this.softwareTypes = softwareTypes;
	}

	public Set<SoftwareLicense> getSoftwareLicenses() {
		return softwareLicenses;
	}

	@JsonIgnore
	public void setSoftwareLicenses(Set<SoftwareLicense> softwareLicenses) {
		this.softwareLicenses = softwareLicenses;
	}

	public Set<Topic> getTopics() {
		return topics;
	}

	@JsonIgnore
	public void setTopics(Set<Topic> topics) {
		this.topics = topics;
	}

    /**
	 * @return the softwareDevelopmentMethodologies
	 */
	public Set<SoftwareDevelopmentMethodology> getSoftwareDevelopmentMethodologies() {
		return softwareDevelopmentMethodologies;
	}

	/**
	 * @param softwareDevelopmentMethodologies the softwareDevelopmentMethodologies to set
	 */
	@JsonIgnore
	public void setSoftwareDevelopmentMethodologies(
			Set<SoftwareDevelopmentMethodology> softwareDevelopmentMethodologies) {
		this.softwareDevelopmentMethodologies = softwareDevelopmentMethodologies;
	}

	/**
     * @return the jirameasurements
     */
    public Set<JiraMetricMeasurement> getJirameasurements() {
        return jirameasurements;
    }

    /**
     * @param jirameasurements the jirameasurements to set
     */
    @JsonIgnore
    public void setJirameasurements(Set<JiraMetricMeasurement> jirameasurements) {
        this.jirameasurements = jirameasurements;
    }

	/**
	 * @return the analysis
	 */
	public List<Analysis> getAnalysis() {
		return analysis;
	}

	/**
	 * @param analysis the analysis to set
	 */
	@JsonIgnore
	public void setAnalysis(List<Analysis> analysis) {
		this.analysis = analysis;
	}

	/**
	 * @param analysis
	 */
	public void addAnalysis(Analysis analysis) {
		this.analysis.add(analysis);
	}
	
		/**
	 * @return the similarity value
	 */
	public Integer getSimilarityValue() {
		return similarityValue;
	}

	/**
	 * @param similarityValue the similarityValue to set
	 */
	@JsonIgnore
	public void setSimilarityValue(Integer similarityValue) {
		this.similarityValue = similarityValue;
	}
	
	/**
	 * Increment the similarityValue
	 */
	public void incrementSimilarityValue(){
		this.similarityValue++;
	}
	
	@Override
	public int compareTo(Project o) {
		return getSimilarityValue().compareTo(o.getSimilarityValue());
	}

	public String getShortName() {
		return super.getNodeKey();
	}
	
	@JsonProperty("shortName")
	@XmlElement(name="shortName")
	public void setShortName(String nodeKey) {
		super.setNodeKey(nodeKey);
	}

	@XmlElement(name="lifeCycleStage")
	public LifeCycleStage getLifeCycleStage() {
		return super.getLifeCycleStage();
	}
	
	
}
