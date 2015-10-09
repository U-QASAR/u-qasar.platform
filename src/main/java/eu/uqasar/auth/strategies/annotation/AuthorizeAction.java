/*
 */

package eu.uqasar.auth.strategies.annotation;

import eu.uqasar.model.role.Role;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Documented
@Inherited
public @interface AuthorizeAction {

	/**
	 * The action that is allowed. The default actions that are supported by
	 * Wicket are <code>RENDER</code> and
	 * <code>ENABLE</code> as defined as constants
	 * of {@link org.apache.wicket.Component}.
	 * 
	 * @see org.apache.wicket.Component#RENDER
	 * @see org.apache.wicket.Component#ENABLE
	 * 
	 * @return the action that is allowed
	 */
	String action();

	/**
	 * The roles for this action.
	 * 
	 * @return the roles for this action. The default is an empty array
	 *         (annotations do not allow null default values)
	 */
	Role[] roles() default {};

	/**
	 * The roles to deny for this action.
	 * 
	 * @return the roles to deny for this action. The default is an empty array
	 *         (annotations do not allow null default values)
	 */
	Role[] deny() default {};

}
