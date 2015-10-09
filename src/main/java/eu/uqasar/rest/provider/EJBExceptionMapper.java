/**
 * 
 */
package eu.uqasar.rest.provider;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.transaction.RollbackException;
import javax.validation.ConstraintViolationException;
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
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

	// inject a logger
	@Inject
	Logger logger;

	@Override
	public Response toResponse(EJBException ex) {
		logger.error(ex.getMessage(), ex);
		
		if (ex.getCause() instanceof PersistenceException) {
			// if we get a persistence related exception from one of our rest
			// services
			if (ex.getCause() instanceof NoResultException
					|| ex.getCause() instanceof EntityNotFoundException) {
				// if the entity was not found or query returned no result -->
				// send 404
				return Response.status(Status.NOT_FOUND)
						.entity(ex.getCause().getMessage()).build();
			}
		} else if (ex instanceof EJBTransactionRolledbackException) {
			if (ex.getCause() != null
					&& ex.getCause() instanceof RollbackException) {
				Throwable cause = ex.getCause();
				if (cause.getCause() != null
						&& cause.getCause() instanceof PersistenceException) {
					cause = cause.getCause();
					// if cause of rollback was a constraint violation return
					// status code 400 --> bad request
					if (cause.getCause() != null
							&& cause.getCause() instanceof ConstraintViolationException) {
						return Response.status(Status.BAD_REQUEST)
								.entity(cause.getCause().getMessage()).build();
					}
				}
			}
		}
//		return Response.serverError().entity(ex.getMessage()).build();
		return Response
				.serverError()
				.entity("An unexpected error has occurred - we apologize for the inconvenience.")
				.build();
	}

}
