package eu.uqasar.web.pages.admin.companies.panels;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
public class CompanyFilterStructure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1306629342832495716L;
	private String name;
	private String shortName;
	private String country;
	
	public CompanyFilterStructure() {

	}

	public CompanyFilterStructure(CompanyFilterPanel panel) {
		this.name = panel.getName();
		this.shortName = panel.getShortName();
		this.country = panel.getCountry();
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "CompanyFilterStructure{" + "name=" + name + "shortName=" + shortName + "country=" + country +"}";
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
