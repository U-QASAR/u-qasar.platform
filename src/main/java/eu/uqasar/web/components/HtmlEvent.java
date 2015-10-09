/**
 * 
 */
package eu.uqasar.web.components;

/**
 *
 * 
 */
public enum HtmlEvent {

	ONCHANGE("change"),

	ONBLUR("blur"),
	
	// these next two are specifically for capturing events on a bootstrap
	// date picker
	ONCHANGEDATE("changeDate"),
	
	ONHIDE("hide");

	private String event;

	private HtmlEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return event;
	}

}
