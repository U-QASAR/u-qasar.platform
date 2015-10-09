/**
 * 
 */
package eu.uqasar.rest.provider;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.solder.logging.Logger;

/**
 *
 * 
 */
@Provider
public class ValidationExceptionMapper implements
		ExceptionMapper<ValidationException> {

	// inject a logger
	@Inject
	Logger logger;

	@Override
	public Response toResponse(ValidationException ex) {
		logger.error(ex.getMessage(), ex);
		return Response.status(Status.BAD_REQUEST).entity(ex.getMessage())
				.build();
	}

}
