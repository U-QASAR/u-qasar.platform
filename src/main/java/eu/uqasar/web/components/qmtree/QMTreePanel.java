package eu.uqasar.web.components.qmtree;

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


import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.qmtree.QMMetric;
import eu.uqasar.model.qmtree.QMQualityIndicator;
import eu.uqasar.model.qmtree.QMQualityObjective;
import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.web.components.qmtree.util.QMTree;
import eu.uqasar.web.components.qmtree.util.QMTreeExpansion;
import eu.uqasar.web.components.qmtree.util.QMTreeProvider;
import eu.uqasar.web.pages.qmtree.panels.filter.QMTreeFilterStructure;

public abstract class QMTreePanel extends Panel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5859602120506082097L;

	@Getter
	private final QMTree tree;

	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> editLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> newQModelLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> newMetricLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> newQualityIndicatorLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> newQualityObjectiveLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> moveUpLink;
	@Getter(AccessLevel.PRIVATE)
	private final BootstrapAjaxLink<String> moveDownLink;
	private final BootstrapAjaxLink<String> deleteLink;
	private final WebMarkupContainer newLinksContainer;

	
	private QMTreeProvider provider;
	
	public QMTreePanel(String id, QMTreeFilterStructure filter) {
		super(id);
		add(tree = createTree(filter));
		
		
		add(editLink = getEditLink());
		add(moveDownLink = getMoveDownLink());
		add(moveUpLink = getMoveUpLink());
		add(deleteLink = getNewDeleteLink());

		newLinksContainer = new WebMarkupContainer("container.tree.node.new");
		newLinksContainer.setOutputMarkupId(true);
		newLinksContainer.add(newQModelLink = getNewQModelLink());
		newLinksContainer
		.add(newQualityObjectiveLink = getNewQualityObjectiveLink());
		newLinksContainer
		.add(newQualityIndicatorLink = getNewQualityIndicatorLink());
		newLinksContainer.add(newMetricLink = getNewMetricLink());

		add(newLinksContainer);
		setOutputMarkupId(true);
	}

	
	
	public void updateFilter(QMTreeFilterStructure filter) {
		provider.setFilter(filter);
	}

	
	private QMTree createTree(QMTreeFilterStructure filter) {
		provider = new QMTreeProvider(filter);
		return new QMTree("tree", provider, new QMTreeExpansionModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onNodeClicked(AjaxRequestTarget target,
					IModel<QMTreeNode> node) {
				updateTreeLinksEnabling(target, node);
				QMTreePanel.this.onNodeClicked(target, node);
			}
		};
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		updateTreeLinksEnabling(null, tree.getSelectedNode());
	}

	private void updateTreeLinksEnabling(AjaxRequestTarget target,
			IModel<QMTreeNode> nodeModel) {
		// creating qmodels is always enabled!
		newQModelLink.setVisible(true);
		if (nodeModel != null && nodeModel.getObject() != null) {
			QMTreeNode node = nodeModel.getObject();
			if (node instanceof QModel) {
				newMetricLink.setVisible(false);
				newQualityIndicatorLink.setVisible(false);
				newQualityObjectiveLink.setVisible(true);
				moveUpLink.setEnabled(false);
				moveDownLink.setEnabled(false);
			} else {
				moveUpLink.setEnabled(true);
				moveDownLink.setEnabled(true);
			}
			if (node instanceof QMQualityObjective) {
				newMetricLink.setVisible(false);
				newQualityIndicatorLink.setVisible(true);
				newQualityObjectiveLink.setVisible(true);
			}
			if (node instanceof QMQualityIndicator) {
				newMetricLink.setVisible(true);
				newQualityIndicatorLink.setVisible(true);
				newQualityObjectiveLink.setVisible(true);
			}
			if (node instanceof QMMetric) {
				newMetricLink.setVisible(true);
				newQualityIndicatorLink.setVisible(true);
				newQualityObjectiveLink.setVisible(true);
			}
			editLink.setEnabled(true);
			deleteLink.setEnabled(true);
		} else {
			editLink.setEnabled(false);
			deleteLink.setEnabled(false);
			moveUpLink.setEnabled(false);
			moveDownLink.setEnabled(false);
		}

		if (target != null) {
			target.add(newLinksContainer);
			target.add(editLink);
			target.add(deleteLink);
			target.add(moveDownLink);
			target.add(moveUpLink);
		}
	}

	private BootstrapAjaxLink<String> getNewDeleteLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.delete", Buttons.Type.Danger) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 2176346425710969753L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				QMTreePanel.this.onDeleteClicked(target, tree.getSelectedNode());
			}
		};
		link.setIconType(IconType.trash);
		link.setOutputMarkupId(true);
		return link;
	}

	public void unselectNode() {
		getTree().unselect();
		updateTreeLinksEnabling(null, tree.getSelectedNode());
	}
	
	protected QMTreeNode getSelectedNode() {
		IModel<QMTreeNode> nodeModel = tree.getSelectedNode();
		return nodeModel != null && nodeModel.getObject() != null ? nodeModel
				.getObject() : null;
	}

	public abstract void onMoveUpClicked(AjaxRequestTarget target,
			IModel<QMTreeNode> node);

	public abstract void onMoveDownClicked(AjaxRequestTarget target,
			IModel<QMTreeNode> node);

	public abstract void onEditClicked(AjaxRequestTarget target,
			IModel<QMTreeNode> node);

	public abstract void onDeleteClicked(AjaxRequestTarget target,
			IModel<QMTreeNode> node);

	public abstract void onNewClicked(AjaxRequestTarget target,
			IModel<QMTreeNode> parent, Class<? extends QMTreeNode> newNodeType);

	public abstract void onNodeClicked(AjaxRequestTarget target,
			IModel<QMTreeNode> node);

	private class QMTreeExpansionModel extends
	AbstractReadOnlyModel<Set<QMTreeNode>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -9049806465736058058L;

		@Override
		public Set<QMTreeNode> getObject() {
			return QMTreeExpansion.get();
		}
	}

}
