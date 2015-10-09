package eu.uqasar.web.components.qmtree.util;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import eu.uqasar.model.qmtree.QMTreeNode;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.web.pages.qmtree.panels.filter.QMTreeFilterStructure;

public class QMTreeProvider implements ITreeProvider<QMTreeNode> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8138485375008639319L;
	
	@Inject
	private QMTreeNodeService treeNodeService;
	
	private QMTreeFilterStructure filter;

	public QMTreeProvider(QMTreeFilterStructure filter) {
		CdiContainer.get().getNonContextualManager().inject(this);
		this.filter = filter;
	}
	
	public void setFilter(QMTreeFilterStructure filter) {
		this.filter = filter;
	}
	
	@Override
	public void detach() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<? extends QMTreeNode> getChildren(QMTreeNode node) {
		return (Iterator<? extends QMTreeNode>) node.getChildren().iterator();
	}

	@Override
	public Iterator<? extends QMTreeNode> getRoots() {
		List<QModel> allQModels = treeNodeService.getAllQModelsFiltered(filter);
		return allQModels.iterator();
	}

	@Override
	public boolean hasChildren(QMTreeNode node) {
		return !node.getChildren().isEmpty();
	}

	@Override
	public IModel<QMTreeNode> model(QMTreeNode node) {
		return new QMTreeModel(node, treeNodeService);
	}

	private static class QMTreeModel extends LoadableDetachableModel<QMTreeNode> {
		

		/**
		 * 
		 */
		private static final long serialVersionUID = -4142269748522932864L;

		private final Long id;
		private final QMTreeNodeService service;

		public QMTreeModel(QMTreeNode foo, QMTreeNodeService service) {
			super(foo);
			id = foo.getId();
			this.service = service;
		}

		@Override
		protected QMTreeNode load() {
			return service.getById(id);
		}

		/**
		 * Important! Models must be identifyable by their contained object.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof QMTreeModel) {
				return ((QMTreeModel) obj).id.equals(id);
			}
			return false;
		}

		/**
		 * Important! Models must be identifyable by their contained object.
		 */
		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}
	
}
