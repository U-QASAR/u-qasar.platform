package eu.uqasar.web.components.behaviour.user;

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


import eu.uqasar.model.user.User;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.provider.UrlProvider;
import javax.inject.Inject;
import static org.apache.wicket.AttributeModifier.VALUELESS_ATTRIBUTE_ADD;
import static org.apache.wicket.AttributeModifier.VALUELESS_ATTRIBUTE_REMOVE;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.value.IValueMap;

/**
 *
 *
 */
public class UserProfilePictureBackgroundBehaviour extends Behavior implements IClusterable {

	/**
	 * Attribute specification.
	 */
	private final String attribute = "style";
	private User user;
	private final boolean caching;
	private final User.PictureDimensions dimension;
	
	@Inject
	UrlProvider urlProvider;

	public UserProfilePictureBackgroundBehaviour(User.PictureDimensions dimension) {
		this(null, dimension, true);
	}

	public UserProfilePictureBackgroundBehaviour(User user, User.PictureDimensions dimension) {
		this(user, dimension, true);
	}

	private UserProfilePictureBackgroundBehaviour(User user, User.PictureDimensions dimension, boolean caching) {
		CdiContainer.get().getNonContextualManager().inject(this);
		if (user == null) {
			if (UQasar.exists() && UQasar.getSession() != null) {
				this.user = UQasar.getSession().getLoggedInUser();
			}
		} else {
			this.user = user;
		}
		this.caching = caching;
		this.dimension = dimension;
		Args.notNull(this.user, "user");
		Args.notNull(this.dimension, "dimension");
		Args.notNull(this.urlProvider, "urlProvider");
	}
	
	private IModel<String> getReplaceModel() {
		final String url = urlProvider.urlFor(this.user.getProfilePictureReference(),
				caching ? this.user.getUncachedProfilePicturePageParameters(dimension)
				: this.user.getProfilePicturePageParameters(dimension));
		final String imageCSS = String.format("background-image: url('%s');", url);
		return Model.of(imageCSS);
	}

	@Override
	public final void onComponentTag(Component component, ComponentTag tag) {
		if (tag.getType() != XmlTag.TagType.CLOSE) {
			replaceAttributeValue(component, tag);
		}
	}

	/**
	 * Checks the given component tag for an instance of the attribute to modify
	 * and if all criteria are met then replace the value of this attribute with
	 * the value of the contained model object.
	 *
	 * @param component The component
	 * @param tag The tag to replace the attribute value for
	 */
    private void replaceAttributeValue(final Component component, final ComponentTag tag) {
		if (isEnabled(component)) {
			final IValueMap attributes = tag.getAttributes();
			final Object replacementValue = getReplacementOrNull(component);

			if (VALUELESS_ATTRIBUTE_ADD == replacementValue) {
				attributes.put(attribute, null);
			} else if (VALUELESS_ATTRIBUTE_REMOVE == replacementValue) {
				attributes.remove(attribute);
			} else {
				final String value = toStringOrNull(attributes.get(attribute));
				final String newValue = newValue(value, toStringOrNull(replacementValue));
				if (newValue != null) {
					attributes.put(attribute, newValue);
				}
			}
		}
	}

	private String newValue(String currentValue, String appendValue) {
		// Short circuit when one of the values is empty: return the other value.
		if (Strings.isEmpty(currentValue)) {
			return appendValue != null ? appendValue : null;
		} else if (Strings.isEmpty(appendValue)) {
			return currentValue != null ? currentValue : null;
		}

        String sb = currentValue + (getSeparator() == null ? "" : getSeparator()) +
                appendValue;
        return sb;
	}

	/**
	 * Gets the separator used by attribute appenders and prependers.
	 *
	 * @return the separator used by attribute appenders and prependers.
	 */
    private String getSeparator() {
		return ";";
	}

	/**
	 * gets replacement with null check.
	 *
	 * @param component
	 * @return replacement value
	 */
	private Object getReplacementOrNull(final Component component) {
		IModel<?> model = getReplaceModel();
		if (model instanceof IComponentAssignedModel) {
			model = ((IComponentAssignedModel<?>) model).wrapOnAssignment(component);
		}
		return (model != null) ? model.getObject() : null;
	}

	/**
	 * gets replacement as a string with null check.
	 *
	 * @param replacementValue
	 * @return replacement value as a string
	 */
	private String toStringOrNull(final Object replacementValue) {
		return (replacementValue != null) ? replacementValue.toString() : null;
	}

}
