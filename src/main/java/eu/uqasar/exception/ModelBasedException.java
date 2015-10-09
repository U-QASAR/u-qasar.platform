/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uqasar.exception;

import eu.uqasar.util.resources.ResourceBundleLocator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 *
 */
public class ModelBasedException extends Exception {

	private Throwable cause = this;

	private IModel<String> model = Model.of("");

	public ModelBasedException() {
		fillInStackTrace();
	}

	public ModelBasedException(final String message) {
		fillInStackTrace();
		model = Model.of(message);
	}

	public ModelBasedException(final String message, Throwable cause) {
		fillInStackTrace();
		this.cause = cause;
		model = Model.of(message);
	}

	public ModelBasedException(IModel<String> messageModel) {
		fillInStackTrace();
		model = messageModel;
	}

	public ModelBasedException(IModel<String> messageModel, Throwable cause) {
		fillInStackTrace();
		this.cause = cause;
		model = messageModel;
	}

	/**
	 * Returns the cause of this throwable or {@code null} if the cause is
	 * nonexistent or unknown. (The cause is the throwable that caused this
	 * throwable to get thrown.)
	 *
	 * <p>
	 * This implementation returns the cause that was supplied via one of the
	 * constructors requiring a {@code Throwable}, or that was set after
	 * creation with the {@link #initCause(Throwable)} method. While it is
	 * typically unnecessary to override this method, a subclass can override it
	 * to return a cause set by some other means. This is appropriate for a
	 * "legacy chained throwable" that predates the addition of chained
	 * exceptions to {@code Throwable}. Note that it is <i>not</i>
	 * necessary to override any of the {@code PrintStackTrace} methods, all of
	 * which invoke the {@code getCause} method to determine the cause of a
	 * throwable.
	 *
	 * @return the cause of this throwable or {@code null} if the cause is
	 * nonexistent or unknown.
	 * @since 1.4
	 */
	@Override
	public synchronized Throwable getCause() {
		return (cause == this ? null : cause);
	}

	@Override
	public String getMessage() {
		return model.getObject();
	}

	public IModel<String> getLabelModel(final String key) {
		return getLabelModel(key, this.getClass());
	}

	public static IModel<String> getLabelModel(final String key, final Class<?> clazz) {
		return ResourceBundleLocator.getLabelModel(clazz, key);
	}
}
