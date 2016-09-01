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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.AbstractEntity;

@NoArgsConstructor
@Setter
@Getter
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
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector = TermVector.YES)
    private String nodeKey;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector = TermVector.YES)
    private String name;

    @Lob
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, termVector = TermVector.YES)
    private String description;

    @XmlTransient
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference("parent")
    protected QMTreeNode parent;

    @XmlElement
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OrderColumn
    @JsonManagedReference("parent")
    @IndexedEmbedded(depth = 3)
    protected List<QMTreeNode> children = new LinkedList<>();

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
    private List<QMTreeNode> getMutableSiblings() {
        return getMutableSiblings(true);
    }

    @JsonIgnore
    private List<QMTreeNode> getMutableSiblings(boolean includeSelf) {
        if (this instanceof QModel) {
            // TODO not sure if QModels cannot have siblings!
            LinkedList<QMTreeNode> list = new LinkedList<>();
            if (includeSelf) {
                list.add(this);
            }
            return list;
        } else {
            if (includeSelf) {
                return getParent().getChildren();
            } else {
                List<QMTreeNode> siblings = getParent()
                        .getChildren();
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
                LinkedList<QMTreeNode> oldParentChildren = (LinkedList) oldParent.getChildren();
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
                LinkedList<QMTreeNode> oldParentChildren = (LinkedList) oldParent.getChildren();
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


    @Override
    public void addChild(IQMTreeNode<String> child) {
        if (!this.children.contains(child)) {
            this.children.add((QMTreeNode) child);
        }
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
    private static QMQualityIndicator getQualityIndicator(
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
    private static QMQualityObjective getQualityObjective(
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
    private static QModel getQModel(IQMTreeNode<String> node) {
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

    public boolean allChildrenCompleted() {
        boolean completed = true;
        List<QMTreeNode> list = getChildren();
        int i = 0;
        while (completed && i < list.size()) {
            completed = completed && list.get(i).isCompleted();
            i++;
        }
        return completed;
    }
}
