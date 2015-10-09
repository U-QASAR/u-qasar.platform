package eu.uqasar.web.components.qmtree;

import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

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

	private final QMTree tree;

	private final BootstrapAjaxLink<String> editLink;
	private final BootstrapAjaxLink<String> newQModelLink;
	private final BootstrapAjaxLink<String> newMetricLink;
	private final BootstrapAjaxLink<String> newQualityIndicatorLink;
	private final BootstrapAjaxLink<String> newQualityObjectiveLink;
	private final BootstrapAjaxLink<String> moveUpLink;
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

	private BootstrapAjaxLink<String> getEditLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.edit", Buttons.Type.Success) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5810958836960583372L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				QMTreePanel.this.onEditClicked(target, tree.getSelectedNode());
			}
		};
		link.setIconType(IconType.pencil);
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getMoveDownLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.move.down", Buttons.Type.Default) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8722070045336679239L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				QMTreePanel.this
				.onMoveDownClicked(target, tree.getSelectedNode());
			}
		};
		link.setIconType(new IconType("chevron-sign-down"));
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getMoveUpLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.move.up", Buttons.Type.Default) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1358484939971486813L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				QMTreePanel.this.onMoveUpClicked(target, tree.getSelectedNode());
			}
		};
		link.setIconType(new IconType("chevron-sign-up"));
		link.setOutputMarkupId(true);
		return link;
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

	private BootstrapAjaxLink<String> getNewQModelLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.new.qmodel", new StringResourceModel(
						"button.tree.new.qmodel", this, null),
						Buttons.Type.Link) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8841475314340477432L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				QMTreePanel.this.onNewClicked(target, tree.getSelectedNode(),
						QModel.class);
			}
		};
		//link.setIconType(new IconType("sitemap"));
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getNewQualityObjectiveLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.new.objective", new StringResourceModel(
						"button.tree.new.objective", this, null),
						Buttons.Type.Link) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4039805850188835764L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				QMTreePanel.this.onNewClicked(target, tree.getSelectedNode(),
						QMQualityObjective.class);
			}
		};
		//link.setIconType(IconType.tasks);
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getNewQualityIndicatorLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.new.indicator", new StringResourceModel(
						"button.tree.new.indicator", this, null),
						Buttons.Type.Link) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5517667987945735645L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				QMTreePanel.this.onNewClicked(target, tree.getSelectedNode(),
						QMQualityIndicator.class);
			}
		};
		//link.setIconType(new IconType("dashboard"));
		link.setOutputMarkupId(true);
		return link;
	}

	private BootstrapAjaxLink<String> getNewMetricLink() {
		BootstrapAjaxLink<String> link = new BootstrapAjaxLink<String>(
				"link.tree.node.new.metric", new StringResourceModel(
						"button.tree.new.metric", this, null),
						Buttons.Type.Link) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3184341389687340579L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				QMTreePanel.this.onNewClicked(target, tree.getSelectedNode(),
						QMMetric.class);
			}
		};
		//link.setIconType(IconType.signal);
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

	public QMTree getTree() {
		return tree;
	}

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
