package eu.uqasar.model.meta;


import javax.persistence.Entity;


@Entity
public class QModelTagData extends MetaData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long tagId;

	private String className;

	public QModelTagData() {
	}

	public QModelTagData(String name) {
		super(name);
	}

	public QModelTagData(MetaData ent) {
		super(ent.getName());
		className = ent.getClass().getName();
		tagId = ent.getId();
	}

	public QModelTagData(String name, String cName, Long tId) {
		super(name);
		className = cName;
		tagId = tId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
}
