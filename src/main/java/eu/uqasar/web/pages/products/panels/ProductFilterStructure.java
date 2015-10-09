package eu.uqasar.web.pages.products.panels;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
public class ProductFilterStructure implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1462933581729253629L;
	private String name;
	private String version;
	private Date releaseDate;


	public ProductFilterStructure(ProductManagementPanel panel) {
		this.name = panel.getName();
		this.releaseDate = panel.getReleaseDate();
		this.version = panel.getVersion();

	}

	public ProductFilterStructure() {

	}

	@Override
	public String toString() {
		return "ProductFilterStructure{" + "name=" + name + ", release date="
				+ releaseDate + ", version=" + version + "}";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void String(String version) {
		this.version = version;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

}
