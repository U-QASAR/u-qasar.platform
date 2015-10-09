package eu.uqasar.model.qmtree;

import java.io.Serializable;
import java.util.List;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.Persistable;

public interface IQMTreeNode<Key extends Serializable> extends Persistable<Long>, Serializable {

	public Key getNodeKey();
	
	public IQMTreeNode<Key> getParent();
	
	public List<? extends IQMTreeNode<Key>> getSiblings();
	
	public List<? extends IQMTreeNode<Key>> getSiblings(boolean includeSelf);
	
	public boolean canChangePositionWithNextSibling();

	public boolean canChangePositionWithNextSibling(boolean changeParents);

	public boolean canChangePositionWithPreviousSibling();

	public boolean canChangePositionWithPreviousSibling(boolean changeParents);
	
	public boolean changePositionWithNextSibling();

	public boolean changePositionWithNextSibling(boolean changeParents);

	public boolean changePositionWithPreviousSibling();

	public boolean changePositionWithPreviousSibling(boolean changeParents);
	
	public List<? extends IQMTreeNode<Key>> getChildren();
	
	public Class<? extends IQMTreeNode<Key>> getChildType();

	public void addChild(IQMTreeNode<Key> child);
	
	public String getName();
	
	public IconType getIconType();

	public boolean getIsCompleted();
}
