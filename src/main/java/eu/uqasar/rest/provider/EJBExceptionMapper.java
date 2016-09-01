package eu.uqasar.rest.provider;

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
