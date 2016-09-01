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


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;
import org.jboss.solder.logging.Logger;

import com.github.slugify.Slugify;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.AbstractEntity;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.util.UQasarUtil.SuggestionType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
@XmlSeeAlso({QualityObjective.class, QualityIndicator.class, Metric.class})
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "@class")
public class TreeNode extends
		AbstractEntity implements ITreeNode<String> {

	private static final Logger logger = Logger.getLogger(TreeNode.class);

	private static final long serialVersionUID = -3121513789680716019L;

	@JsonIgnore
	@XmlTransient
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
    String nodeKey;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
    String name;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
    @Lob
    String description;

    @XmlTransient
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	@Enumerated(EnumType.STRING)
    private QualityStatus qualityStatus = QualityStatus.Gray;

    @XmlTransient
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	@Enumerated(EnumType.STRING)
    private LifeCycleStage lifeCycleStage = LifeCycleStage.Requirements;

    @XmlTransient
    @ManyToOne(cascade = CascadeType.ALL)
    @IndexedEmbedded(depth = 3)
    @JsonBackReference("parent")
    private TreeNode parent;

	// Set the type of the suggestion and the belonging value, but do not store 
	// those to the DB as they are computed on the fly 
    @XmlTransient
    private SuggestionType suggestionType;
	
    @XmlTransient
    private String suggestionValue;
	
	@XmlElement
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="parent_id")
	@OrderColumn
	@JsonManagedReference("parent")
	@IndexedEmbedded(depth = 3)
    private List<TreeNode> children = new LinkedList<>();
	
	protected TreeNode() {
	}

	public TreeNode(final TreeNode parent) {
		if (parent == null) {
			throw new IllegalArgumentException("parent required");
		}

		this.parent = parent;
		registerInChildren();
	}

	private void registerInChildren() {
		if (this.parent != null) {
			this.parent.children.add(this);
		}
	}

	@JsonIgnore
	@Override
	public List<TreeNode> getSiblings() {
		return getSiblings(true);
	}

	@JsonIgnore
	@Override
	public List<TreeNode> getSiblings(boolean includeSelf) {
		return Collections.unmodifiableList(getMutableSiblings(includeSelf));
	}
	
	@JsonIgnore
    private List<TreeNode> getMutableSiblings() {
		return getMutableSiblings(true);
	}
	
	@JsonIgnore
    private List<TreeNode> getMutableSiblings(boolean includeSelf) {
		if (this instanceof Project) {
			// TODO not sure if projects cannot have siblings!
			LinkedList<TreeNode> list = new LinkedList<>();
			if (includeSelf) {
				list.add(this);
			}
			return list;
		} else {
			if (includeSelf) {
				return getParent().getChildren();
			} else {
				LinkedList<TreeNode> siblings = (LinkedList<TreeNode>) getParent()
						.getChildren().clone();
				siblings.remove(this);
				return siblings;
			}
		}
	}

	@Override
	public boolean canChangePositionWithNextSibling() {
		return canChangePositionWithNextSibling(true);
	}

	@Override
	public boolean canChangePositionWithNextSibling(boolean changeParents) {
		LinkedList<TreeNode> directSiblings = (LinkedList<TreeNode>) getMutableSiblings();
		int currentIndex = directSiblings.indexOf(this);
		int newIndex = currentIndex + 1;
		if (newIndex < directSiblings.size()) {
			// switch currently selected node with the next one
			return true;
		} else if (newIndex >= directSiblings.size() && changeParents) {
			// add currently selected node as first entry to the next parent
			// sibling
			LinkedList<TreeNode> parentSiblings = (LinkedList<TreeNode>) this
					.getParent().getMutableSiblings();
			int parentIndex = parentSiblings.indexOf(this.getParent());
			int newParentIndex = parentIndex + 1;
			if (newParentIndex < parentSiblings.size()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean changePositionWithNextSibling() {
		return changePositionWithNextSibling(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean changePositionWithNextSibling(boolean changeParents) {
		LinkedList<TreeNode> directSiblings = (LinkedList<TreeNode>) getMutableSiblings();
		int currentIndex = directSiblings.indexOf(this);
		int newIndex = currentIndex + 1;
		if (newIndex < directSiblings.size()) {
			// switch currently selected node with the next one
			TreeNode movedNode = directSiblings.remove(currentIndex);
			directSiblings.add(newIndex, movedNode);
			getParent().setChildren(directSiblings);
			logger.info(String.format("Moving %s from index %s to %s", this, currentIndex, newIndex));
			return true;
		} else if (newIndex >= directSiblings.size() && changeParents) {
			// add currently selected node as first entry to the next parent
			// sibling
			LinkedList<TreeNode> parentSiblings = (LinkedList<TreeNode>) this
					.getParent().getMutableSiblings();
			int parentIndex = parentSiblings.indexOf(this.getParent());
			int newParentIndex = parentIndex + 1;
			if (newParentIndex < parentSiblings.size()) {
				TreeNode oldParent = this.getParent();
				LinkedList<TreeNode> oldParentChildren = oldParent.getChildren();
				oldParentChildren.removeLast();
				oldParent.setChildren(oldParentChildren);
				
				TreeNode newParent = parentSiblings.get(newParentIndex);
				logger.info(String.format("Moving %s from parent %s to %s", this, this.getParent(), newParent));
				this.setParent(newParent);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canChangePositionWithPreviousSibling() {
		return canChangePositionWithPreviousSibling(true);
	}

	@Override
	public boolean canChangePositionWithPreviousSibling(boolean changeParents) {
		LinkedList<TreeNode> directSiblings = (LinkedList<TreeNode>) getMutableSiblings();
		int currentIndex = directSiblings.indexOf(this);
		if (currentIndex > 0) {
			// switch currently selected node with the previous one
			return true;
		} else if (currentIndex == 0 && changeParents) {
			// add currently selected node as last entry to the previous
			// parent sibling
			LinkedList<TreeNode> parentSiblings = (LinkedList<TreeNode>) this
					.getParent().getMutableSiblings();
			int parentIndex = parentSiblings.indexOf(this.getParent());
			return parentIndex > 0;
		}
		return false;
	}

	@Override
	public boolean changePositionWithPreviousSibling() {
		return changePositionWithPreviousSibling(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean changePositionWithPreviousSibling(boolean changeParents) {
		LinkedList<TreeNode> directSiblings = (LinkedList<TreeNode>) getMutableSiblings();
		int currentIndex = directSiblings.indexOf(this);
		int newIndex = currentIndex - 1;
		if (currentIndex > 0) {
			// switch currently selected node with the previous one
			TreeNode movedNode = directSiblings.remove(currentIndex);
			directSiblings.add(newIndex, movedNode);
			getParent().setChildren(directSiblings);
			logger.info(String.format("Moving %s from index %s to %s", this, currentIndex, newIndex));
			return true;
		} else if (currentIndex == 0 && changeParents) {
			// add currently selected node as last entry to the previous
			// parent sibling
			LinkedList<TreeNode> parentSiblings = (LinkedList<TreeNode>) this
					.getParent().getMutableSiblings();
			int parentIndex = parentSiblings.indexOf(this.getParent());
			int newParentIndex = parentIndex - 1;
			if (parentIndex > 0) {
				TreeNode oldParent = this.getParent();
				LinkedList<TreeNode> oldParentChildren = oldParent.getChildren();
				oldParentChildren.remove(0);
				oldParent.setChildren(oldParentChildren);

				TreeNode newParent = parentSiblings.get(newParentIndex);
				logger.info(String.format("Moving %s from parent %s to %s", this, this.getParent(), newParent));
				this.setParent(newParent);
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the suggestionType
	 */
	public SuggestionType getSuggestionType() {
		return suggestionType;
	}

	/**
	 * @param suggestionType the suggestionType to set
	 */
	@JsonIgnore
	public void setSuggestionType(SuggestionType suggestionType) {
		this.suggestionType = suggestionType;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@JsonManagedReference("parent")
	@Override
	public LinkedList<TreeNode> getChildren() {
		if (children instanceof LinkedList) {
			return (LinkedList<TreeNode>) children;
		} else {
			return new LinkedList<>(children);
		}
	}

	@JsonIgnore
	@JsonManagedReference("parent")
	public void setChildren(List<TreeNode> children) {
		if (children instanceof LinkedList) {
			this.children = children;
		} else {
			this.children = new LinkedList<>(children);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@JsonIgnore
	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	@JsonBackReference("parent")
	@Override
	public TreeNode getParent() {
		return parent;
	}

	@JsonIgnore
	@JsonBackReference("parent")
	public void setParent(TreeNode parent) {
		this.parent = parent;
		this.parent.children.add(this);
	}

	@Override
	public String getNodeKey() {
		if (StringUtils.isBlank(nodeKey)) {
			nodeKey = Slugify.slugify(getName());
		}
		return nodeKey;
	}

	@JsonIgnore
    void setNodeKey(String mKey) {
		this.nodeKey = mKey;
	}

	@Override
	public QualityStatus getQualityStatus() {
		if (qualityStatus == null)
			qualityStatus = QualityStatus.Gray;
		return this.qualityStatus;
	}

	@JsonIgnore
    void setQualityStatus(QualityStatus status) {
		this.qualityStatus = status;
	}

	@Override
	public void addChild(ITreeNode<String> child) {
		if (!this.children.contains(child)) {
			this.children.add((TreeNode) child);
		}
	}

	public String getDescription() {
		if (description == null) {
			description = "";
		}
		return description;
	}

	@JsonIgnore
	public void setDescription(String description) {
		this.description = description;
	}

	public LifeCycleStage getLifeCycleStage() {
		if (lifeCycleStage==null)
			lifeCycleStage = LifeCycleStage.Requirements;
		return lifeCycleStage;
	}

	@JsonIgnore
	public void setLifeCycleStage(LifeCycleStage lifeCycleStage) {
		this.lifeCycleStage = lifeCycleStage;
	}

	@JsonIgnore
	public final String getChildrenString() {
		return getChildrenString("", "");
	}

	@JsonIgnore
	public final String getChildrenString(final String prefix,
			final String suffix) {
		String builder = prefix +
				StringUtils.join(getChildren(), ", ") +
				suffix;
		return builder;
	}

	@JsonIgnore
	public Project getProject() {
		return getProject(this);
	}

	@JsonIgnore
	public QualityObjective getQualityObjective() {
		return getQualityObjective(this);
	}

	@JsonIgnore
	public QualityIndicator getQualityIndicator() {
		return getQualityIndicator(this);
	}

	@JsonIgnore
    private static QualityIndicator getQualityIndicator(
            ITreeNode<String> node) {
		if (node instanceof QualityIndicator) {
			return (QualityIndicator) node;
		}
		if (node.getParent() == null) {
			return null;
		}
		return getQualityIndicator(node.getParent());
	}

	@JsonIgnore
    private static QualityObjective getQualityObjective(
            ITreeNode<String> node) {
		if (node instanceof QualityObjective) {
			return (QualityObjective) node;
		}
		if (node.getParent() == null) {
			return null;
		}
		return getQualityObjective(node.getParent());
	}

	@JsonIgnore
    private static Project getProject(ITreeNode<String> node) {
		if (node.getParent() == null || node instanceof Project) {
			return (Project) node;
		}
		return getProject(node.getParent());
	}

	@Override
	public String toString() {
		return name;
	}

	@JsonIgnore
	@Override
	public Class<? extends ITreeNode<String>> getChildType() {
		return Project.class;
	}

	@JsonIgnore
	@Override
	public IconType getIconType() {
		return IconType.globe;
	}
	
	public void removeChild(TreeNode node) {
		children.remove(node);
	}
	
	public void removeChild(long id) {
		children.remove(id);
	}

	/**
	 * @return the suggestionValue
	 */
	@JsonIgnore
	public String getSuggestionValue() {
		return suggestionValue;
	}

	/**
	 * @param suggestionValue the suggestionValue to set
	 */
	@JsonIgnore
	public void setSuggestionValue(String suggestionValue) {
		this.suggestionValue = suggestionValue;
	}

}
