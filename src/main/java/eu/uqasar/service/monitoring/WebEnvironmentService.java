/*
 */
package eu.uqasar.service.monitoring;

import eu.uqasar.model.monitoring.WebEnvironment;
import eu.uqasar.service.AbstractService;
import javax.ejb.Stateless;

/**
 *
 *
 */
@Stateless
public class WebEnvironmentService extends AbstractService<WebEnvironment> {

	public WebEnvironmentService() {
		super(WebEnvironment.class);
	}
}
