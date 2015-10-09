package eu.uqasar.model.tree;

import java.io.Serializable;
import java.util.List;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import eu.uqasar.model.Persistable;

public interface ITreeNode<Key extends Serializable> extends Persistable<Long>, Serializable {

	public Key getNodeKey();
	
	public ITreeNode<Key> getParent();
	
	public List<? extends ITreeNode<Key>> getSiblings();
	
	public List<? extends ITreeNode<Key>> getSiblings(boolean includeSelf);
	
	public boolean canChangePositionWithNextSibling();

	public boolean canChangePositionWithNextSibling(boolean changeParents);

	public boolean canChangePositionWithPreviousSibling();

	public boolean canChangePositionWithPreviousSibling(boolean changeParents);
	
	public boolean changePositionWithNextSibling();

	public boolean changePositionWithNextSibling(boolean changeParents);

	public boolean changePositionWithPreviousSibling();

	public boolean changePositionWithPreviousSibling(boolean changeParents);
	
	public List<? extends ITreeNode<Key>> getChildren();
	
	public Class<? extends ITreeNode<Key>> getChildType();

	public void addChild(ITreeNode<Key> child);
	
	public String getName();
	
	public IconType getIconType();
	
	public QualityStatus getQualityStatus();

}
