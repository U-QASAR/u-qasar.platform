package eu.uqasar.model.qmtree;

import static javax.persistence.CascadeType.MERGE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.lifecycle.RupStage;
import eu.uqasar.model.meta.QModelTagData;
import eu.uqasar.model.quality.indicator.Paradigm;
import eu.uqasar.model.quality.indicator.Purpose;
import eu.uqasar.model.quality.indicator.Version;
import eu.uqasar.model.role.Role;

@Entity
@XmlTransient
public abstract class QMBaseIndicator extends QMTreeNode {

	
	private static final long serialVersionUID = -6441961332152113221L;

	@Enumerated(EnumType.STRING)
	private Purpose indicatorPurpose = Purpose.Process;

	@Enumerated(EnumType.STRING)
	private Paradigm paradigm = Paradigm.Waterfall;

	@XmlTransient	
	@Enumerated(EnumType.STRING)
	private LifeCycleStage lifeCycleStage = LifeCycleStage.Requirements;

	@XmlTransient
	@Enumerated(EnumType.STRING)
	private RupStage rupStage = RupStage.Inception;

	@Enumerated(EnumType.STRING)
	private Version version = Version.Prototype;
	
	@XmlTransient
	@Column
    @ElementCollection(targetClass=Role.class)
	private List<Role> targetAudience;

	private Double lowerLimit = (double) 0;
	
	private Double upperLimit = (double) 0;
	
	//Sprint 2. Added QProject attributes
	@XmlElement
	private float weight = 1;
	
	//Sprint 2. Added QProject attributes
	@XmlElement
	private float targetValue = 0;
	
	//Sprint 3. Tags for Quality Model entities
	@XmlTransient
	@ManyToMany(cascade = MERGE)
	private Set<QModelTagData> qModelTagData = new HashSet<>();
	
	protected QMBaseIndicator() {
	}

	public QMBaseIndicator(final QMTreeNode parent) {
		super(parent);
	}

	public Purpose getIndicatorPurpose() {
		if (indicatorPurpose == null) {
			indicatorPurpose = Purpose.Process;
		}
		return indicatorPurpose;
	}

	@JsonIgnore
	public void setIndicatorPurpose(Purpose indicatorPurpose) {
		this.indicatorPurpose = indicatorPurpose;
	}

	public double getLowerLimit() {
		if (lowerLimit == null){
			lowerLimit = (double) 0;
		}
		return lowerLimit;
	}

	@JsonIgnore
	public void setLowerLimit(double lowerlimit) {
		this.lowerLimit = lowerlimit;
	}

	public double getUpperLimit() {
		if (upperLimit == null){
			upperLimit = (double) 0;
		}
		return upperLimit;
	}

	@JsonIgnore
	public void setUpperLimit(double upperlimit) {
		this.upperLimit = upperlimit;
	}
	
	public LifeCycleStage getLifeCycleStage() {
		if (lifeCycleStage == null){
			lifeCycleStage = LifeCycleStage.Requirements;
		}
		return lifeCycleStage;
	}

	@JsonIgnore
	public void setLifeCycleStage(LifeCycleStage lifeCycleStage) {
		this.lifeCycleStage = lifeCycleStage;
	}
	
	public List<Role> getTargetAudience() {
		return targetAudience;
	}

	@JsonIgnore
	public void setTargetAudience(List<Role> targetAudience) {
		this.targetAudience = targetAudience;
	}
	
	public Version getVersion() {
		if (version == null) {
			this.version = Version.Prototype;
		}
		return version;
	}
	
	@JsonIgnore
	public void setVersion(Version version) {
		this.version = version;
	}

	public RupStage getRupStage() {
		if (rupStage == null){
			this.rupStage = RupStage.Inception;
		}
		return rupStage;
	}

	@JsonIgnore
	public void setRupStage(RupStage rupStage) {
		this.rupStage = rupStage;
	}
	
	public Paradigm getParadigm() {
		if (paradigm == null){
			this.paradigm = Paradigm.Waterfall;
		}
		return paradigm;
	}

	@JsonIgnore
	public void setParadigm(Paradigm paradigm) {
		this.paradigm = paradigm;
	}
	
	public float getWeight() {
		return weight;
	}

	@JsonIgnore
	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getTargetValue() {
		return targetValue;
	}
	
	@JsonIgnore
	public void setTargetValue(float targetValue) {
		this.targetValue = targetValue;
	}

	/**
	 * getQModelTagData get the Quality Model tags.
	 * @return collection of tags
	 */
	@JsonIgnore
	public Set<QModelTagData> getQModelTagData() {
		return qModelTagData;
	}

	/**
	 * setQModelTagData set the Quality Model tags.
	 * @param qmtd collection of tags
	 */
	@JsonIgnore
	public void setQModelTagData(Set<QModelTagData> qmtd) {
		qModelTagData = qmtd;
	}	
	
}