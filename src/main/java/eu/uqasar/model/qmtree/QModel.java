package eu.uqasar.model.qmtree;

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

import lombok.Getter;
import lombok.Setter;
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


@Setter
@Getter
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
		List<QMTreeNode> nodes = new LinkedList<>();

		if (children != null){
			if (children.isEmpty()){
			isCompleted = false;	
			} else {
				for (QMQualityObjective qo : children) {
					isCompleted = isCompleted && qo.isCompleted();
					nodes.add(qo);
				}
			}
		} else {
			isCompleted = false;
		}
		this.setChildren(nodes);
		this.setCompleted(isCompleted);
	}


	public QModel() {
		this.setCompleted(false);
	}

	public QModel(final String name, final String key) {
		this.setName(name);
		this.setNodeKey(key);
		this.setCompleted(false);
	}


	@JsonIgnore
    private String getAbbreviatedName(int maxLength) {
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


	public String getShortName() {
		return super.getNodeKey();
	}
	
	@JsonProperty("shortName")
	@XmlElement(name="shortName")
    private void setShortName(String nodeKey) {
		super.setNodeKey(nodeKey);
	}
	
	
}
