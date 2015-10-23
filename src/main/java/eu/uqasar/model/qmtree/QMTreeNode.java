package eu.uqasar.model.qmtree;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
@XmlSeeAlso({QMQualityObjective.class, QMQualityIndicator.class, QMMetric.class})
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "@class")
public class QMTreeNode extends
		AbstractEntity implements IQMTreeNode<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6082275378485534254L;
	
	private static Logger logger = Logger.getLogger(QMTreeNode.class);

	/**
	 * isCompleted=true if the entity has associated subentities. 
	 */
	@XmlTransient
	private boolean isCompleted;

	@XmlTransient
	@JsonIgnore
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String nodeKey;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String name;

	@Lob
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector=TermVector.YES)
	private String description;

	@XmlTransient
	@ManyToOne(cascade = CascadeType.ALL)
	@JsonBackReference("parent")
	protected QMTreeNode parent;

	@XmlElement
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="parent_id")
	@OrderColumn
	@JsonManagedReference("parent")
	@IndexedEmbedded(depth = 3)
	protected List<QMTreeNode> children = new LinkedList<>();
	
	public QMTreeNode() {
	
	}

	public QMTreeNode(final QMTreeNode parent) {
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
	public List<QMTreeNode> getSiblings() {
		return getSiblings(true);
	}

	@JsonIgnore
	@Override
	public List<QMTreeNode> getSiblings(boolean includeSelf) {
		return Collections.unmodifiableList(getMutableSiblings(includeSelf));
	}
	
	@JsonIgnore
	protected List<QMTreeNode> getMutableSiblings() {
		return getMutableSiblings(true);
	}
	
	@JsonIgnore
	protected List<QMTreeNode> getMutableSiblings(boolean includeSelf) {
		if (this instanceof QModel) {
			// TODO not sure if QModels cannot have siblings!
			LinkedList<QMTreeNode> list = new LinkedList<>();
			if (includeSelf) {
				list.add((QMTreeNode) this);
			}
			return list;
		} else {
			if (includeSelf) {
				return (LinkedList<QMTreeNode>) getParent().getChildren();
			} else {
				LinkedList<QMTreeNode> siblings = (LinkedList<QMTreeNode>) getParent()
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
		LinkedList<QMTreeNode> directSiblings = (LinkedList<QMTreeNode>) getMutableSiblings();
		int currentIndex = directSiblings.indexOf(this);
		int newIndex = currentIndex + 1;
		if (newIndex < directSiblings.size()) {
			// switch currently selected node with the next one
			return true;
		} else if (newIndex >= directSiblings.size() && changeParents) {
			// add currently selected node as first entry to the next parent
			// sibling
			LinkedList<QMTreeNode> parentSiblings = (LinkedList<QMTreeNode>) this
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
		LinkedList<QMTreeNode> directSiblings = (LinkedList<QMTreeNode>) getMutableSiblings();
		int currentIndex = directSiblings.indexOf(this);
		int newIndex = currentIndex + 1;
		if (newIndex < directSiblings.size()) {
			// switch currently selected node with the next one
			QMTreeNode movedNode = directSiblings.remove(currentIndex);
			directSiblings.add(newIndex, movedNode);
			getParent().setChildren(directSiblings);
			logger.info(String.format("Moving %s from index %s to %s", this, currentIndex, newIndex));
			return true;
		} else if (newIndex >= directSiblings.size() && changeParents) {
			// add currently selected node as first entry to the next parent
			// sibling
			LinkedList<QMTreeNode> parentSiblings = (LinkedList<QMTreeNode>) this
					.getParent().getMutableSiblings();
			int parentIndex = parentSiblings.indexOf(this.getParent());
			int newParentIndex = parentIndex + 1;
			if (newParentIndex < parentSiblings.size()) {
				QMTreeNode oldParent = this.getParent();
				LinkedList<QMTreeNode> oldParentChildren = oldParent.getChildren();
				oldParentChildren.removeLast();
				oldParent.setChildren(oldParentChildren);
				
				QMTreeNode newParent = parentSiblings.get(newParentIndex);
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
		LinkedList<QMTreeNode> directSiblings = (LinkedList<QMTreeNode>) getMutableSiblings();
		int currentIndex = directSiblings.indexOf(this);
		if (currentIndex > 0) {
			// switch currently selected node with the previous one
			return true;
		} else if (currentIndex == 0 && changeParents) {
			// add currently selected node as last entry to the previous
			// parent sibling
			LinkedList<QMTreeNode> parentSiblings = (LinkedList<QMTreeNode>) this
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
		LinkedList<QMTreeNode> directSiblings = (LinkedList<QMTreeNode>) getMutableSiblings();
		int currentIndex = directSiblings.indexOf(this);
		int newIndex = currentIndex - 1;
		if (currentIndex > 0) {
			// switch currently selected node with the previous one
			QMTreeNode movedNode = directSiblings.remove(currentIndex);
			directSiblings.add(newIndex, movedNode);
			getParent().setChildren(directSiblings);
			logger.info(String.format("Moving %s from index %s to %s", this, currentIndex, newIndex));
			return true;
		} else if (currentIndex == 0 && changeParents) {
			// add currently selected node as last entry to the previous
			// parent sibling
			LinkedList<QMTreeNode> parentSiblings = (LinkedList<QMTreeNode>) this
					.getParent().getMutableSiblings();
			int parentIndex = parentSiblings.indexOf(this.getParent());
			int newParentIndex = parentIndex - 1;
			if (parentIndex > 0) {
				QMTreeNode oldParent = this.getParent();
				LinkedList<QMTreeNode> oldParentChildren = oldParent.getChildren();
				oldParentChildren.remove(0);
				oldParent.setChildren(oldParentChildren);

				QMTreeNode newParent = parentSiblings.get(newParentIndex);
				logger.info(String.format("Moving %s from parent %s to %s", this, this.getParent(), newParent));
				this.setParent(newParent);
				return true;
			}
		}
		return false;
	}

	
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@JsonManagedReference("parent")
	@Override
	public LinkedList<QMTreeNode> getChildren() {
		if (children instanceof LinkedList) {
			return (LinkedList<QMTreeNode>) children;
		} else {
			return new LinkedList<>(children);
		}
	}

	@JsonIgnore
	@JsonManagedReference("parent")
	public void setChildren(List<QMTreeNode> children) {
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
	public QMTreeNode getParent() {
		return parent;
	}

	
	@JsonIgnore
	@JsonBackReference("parent")
	public void setParent(QMTreeNode parent) {
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

	public void setNodeKey(String mKey) {
		this.nodeKey = mKey;
	}

	@Override
	public void addChild(IQMTreeNode<String> child) {
		if (!this.children.contains(child)) {
			this.children.add((QMTreeNode) child);
		}
	}

	public String getDescription() {
		if (description == null) {
			description = "";
		}
		return description;
	}

	@JsonIgnore
	public void setDescription(final String description) {
		this.description = description;
	}

	@JsonIgnore
	public final String getChildrenString() {
		return getChildrenString("", "");
	}

	@JsonIgnore
	public final String getChildrenString(final String prefix,
			final String suffix) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(prefix);
		builder.append(StringUtils.join(getChildren(), ", "));
		builder.append(suffix);
		return builder.toString();
	}

	@JsonIgnore
	public QModel getQModel() {
		return getQModel(this);
	}

	@JsonIgnore
	public QMQualityObjective getQualityObjective() {
		return getQualityObjective(this);
	}

	@JsonIgnore
	public QMQualityIndicator getQualityIndicator() {
		return getQualityIndicator(this);
	}

	@JsonIgnore
	protected static QMQualityIndicator getQualityIndicator(
			IQMTreeNode<String> node) {
		if (node instanceof QMQualityIndicator) {
			return (QMQualityIndicator) node;
		}
		if (node.getParent() == null) {
			return null;
		}
		return getQualityIndicator(node.getParent());
	}

	@JsonIgnore
	protected static QMQualityObjective getQualityObjective(
			IQMTreeNode<String> node) {
		if (node instanceof QMQualityObjective) {
			return (QMQualityObjective) node;
		}
		if (node.getParent() == null) {
			return null;
		}
		return getQualityObjective(node.getParent());
	}

	@JsonIgnore
	protected static QModel getQModel(IQMTreeNode<String> node) {
		if (node.getParent() == null || node instanceof QModel) {
			return (QModel) node;
		}
		return getQModel(node.getParent());
	}

	@Override
	public String toString() {
		return name;
	}

	@JsonIgnore
	@Override
	public Class<? extends IQMTreeNode<String>> getChildType() {
		return QModel.class;
	}

	@JsonIgnore
	@Override
	public IconType getIconType() {
		return IconType.globe;
	}
	
	@XmlTransient
	@JsonIgnore
	public boolean getIsCompleted() {
		return isCompleted;
	}

	@JsonIgnore
	public void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean allChildrenCompleted() {
		boolean completed = true;
		LinkedList<QMTreeNode> list = getChildren();
		int i = 0;
		while (completed && i < list.size()){
			completed = completed && list.get(i).getIsCompleted();
			i++;
		}
		return completed;
	}
}
