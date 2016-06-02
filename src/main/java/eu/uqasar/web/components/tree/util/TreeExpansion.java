package eu.uqasar.web.components.tree.util;

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


import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;

import eu.uqasar.model.tree.TreeNode;

public class TreeExpansion implements Set<TreeNode>, Serializable {
	private static final long serialVersionUID = 1L;

	private static final MetaDataKey<TreeExpansion> KEY = new MetaDataKey<TreeExpansion>() {
		private static final long serialVersionUID = 1L;
	};

	private final Set<Long> ids = new HashSet<>();

	private boolean inverse;

	public void expandAll() {
		ids.clear();
		inverse = true;
	}

	public void collapseAll() {
		ids.clear();
		inverse = false;
	}

	@Override
	public boolean add(TreeNode node) {
		if (inverse) {
			return ids.remove(node.getId());
		} else {
			return ids.add(node.getId());
		}
	}

	@Override
	public boolean remove(Object o) {
		@SuppressWarnings("unchecked")
		TreeNode node = (TreeNode) o;

		if (inverse) {
			return ids.add(node.getId());
		} else {
			return ids.remove(node.getId());
		}
	}

	@Override
	public boolean contains(Object o) {
		@SuppressWarnings("unchecked")
		TreeNode node = (TreeNode) o;

		if (inverse) {
			return !ids.contains(node.getId());
		} else {
			return ids.contains(node.getId());
		}
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <A> A[] toArray(A[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<TreeNode> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends TreeNode> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the expansion for the session.
	 * 
	 * @return expansion
	 */
	public static TreeExpansion get() {
		TreeExpansion expansion = Session.get().getMetaData(KEY);
		if (expansion == null) {
			expansion = new TreeExpansion();

			Session.get().setMetaData(KEY, expansion);
		}
		return expansion;
	}

}
