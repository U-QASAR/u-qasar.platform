package eu.uqasar.model.qmtree;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
import org.joda.time.DateTime;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.company.Company;


@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "description", "shortName", "edition", "companyId", "children"})
@Table(name="qmodel")
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
@Indexed
public class QModel extends QMTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2452157839037690716L;

	public static final IconType ICON = new IconType("sitemap");

	@XmlTransient
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private Date updateDate = DateTime.now().plusDays(-2).toDate();

	@XmlTransient
	private QModelStatus isActive = QModelStatus.NotActive;

	private Long companyId;

	@XmlTransient
	@ManyToOne
	private Company company;

	//TODO Define Type of edition field, or how it's going to be generated
	private String edition;

	
	@JsonCreator
	public QModel(
			@JsonProperty("companyId") final Long companyId,
			@JsonProperty("description") final String description,
			@JsonProperty("name") final String name,
			@JsonProperty("shortName") final String key,
			@JsonProperty("children") final List<QMQualityObjective> children) {


		this.setCompanyId(companyId);
		this.setDescription(description);
		this.setName(name);
		this.setShortName(key);

		boolean isCompleted = true;
		List<QMTreeNode> nodes = new LinkedList<QMTreeNode>();

		if (children != null){
			if (children.isEmpty()){
			isCompleted = false;	
			} else {
			Iterator<QMQualityObjective> it = children.iterator();
			while (it.hasNext()){
				QMQualityObjective qo = (QMQualityObjective)it.next();
				isCompleted = isCompleted && qo.getIsCompleted();
				nodes.add((QMTreeNode)qo);
			}
			}
		} else {
			isCompleted = false;
		}
		this.setChildren(nodes);
		this.setIsCompleted(isCompleted);
	}


	public QModel() {
		this.setIsCompleted(false);
	}

	public QModel(final String name, final String key) {
		this.setName(name);
		this.setNodeKey(key);
		this.setIsCompleted(false);
	}


	@JsonIgnore
	public String getAbbreviatedName(int maxLength) {
		return StringUtils.abbreviate(getName(), maxLength);
	}

	@JsonIgnore
	public String getAbbreviatedName() {
		return getAbbreviatedName(48);
	}

	@JsonIgnore
	@Override
	public IconType getIconType() {
		return ICON;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	@JsonIgnore
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getEdition() {
		if (edition == null || edition.equals("")) {
			edition="0.0";
		}
		return edition;
	}

	@JsonIgnore
	public void setEdition(String edition) {
		this.edition = edition;
	}

	@JsonIgnore
	public QModelStatus getIsActive() {
		return isActive;
	}

	@JsonIgnore
	public void setIsActive(QModelStatus isActive) {
		this.isActive = isActive;
	}

	public Long getCompanyId() {
		if (companyId == null){
			companyId = Long.valueOf(0);
			if (company != null) {
				companyId = company.getId();
			}
		}
		return companyId;
	}

	@JsonIgnore
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	/**
	 * @return the company
	 */
	@JsonIgnore
	public Company getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	@JsonIgnore
	public void setCompany(Company company) {
		this.company = company;
	}

	
	public String getShortName() {
		return super.getNodeKey();
	}
	
	@JsonProperty("shortName")
	@XmlElement(name="shortName")
	public void setShortName(String nodeKey) {
		super.setNodeKey(nodeKey);
	}
	
	
}
