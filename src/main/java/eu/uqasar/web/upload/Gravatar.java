package eu.uqasar.web.upload;

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


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * A gravatar is a dynamic image resource that is requested from the
 * gravatar.com server. This class calculates the gravatar url and fetches
 * gravatar images. See http://en.gravatar.com/site/implement/url .
 * 
 * This class is thread-safe, Gravatar objects can be shared.
 * 
 * Usage example:
 * 
 * <code>
 * Gravatar gravatar = new Gravatar();
 * gravatar.setSize(50);
 * gravatar.setRating(GravatarRating.GENERAL_AUDIENCES);
 * gravatar.setDefaultImage(GravatarDefaultImage.IDENTICON);
 * String url = gravatar.getUrl("iHaveAn@email.com");
 * byte[] jpg = gravatar.download("info@ralfebert.de");
 * </code>
 */
@Setter
public final class Gravatar implements Serializable {

	private final static int DEFAULT_SIZE = 80;
	private final static String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
	private static final GravatarRating DEFAULT_RATING = GravatarRating.GENERAL_AUDIENCES;
	private static final GravatarDefaultImage DEFAULT_DEFAULT_IMAGE = GravatarDefaultImage.HTTP_404;

	private int size = DEFAULT_SIZE;
	private GravatarRating rating = DEFAULT_RATING;
	private GravatarDefaultImage defaultImage = DEFAULT_DEFAULT_IMAGE;

	/**
	 * Returns the Gravatar URL for the given email address.
	 */
    private String getUrl(String email) {
		Validate.notNull(email, "email");

		// hexadecimal MD5 hash of the requested user's lowercased email address
		// with all whitespace trimmed
		String emailHash = DigestUtils.md5Hex(email.toLowerCase().trim());
		String params = formatUrlParameters();
		return GRAVATAR_URL + emailHash + ".png" + params;
	}

	/**
	 * Downloads the gravatar for the given URL using Java {@link URL} and
	 * returns a byte array containing the gravatar jpg, returns null if no
	 * gravatar was found.
	 */
	public byte[] download(String email) throws GravatarDownloadException {
		InputStream stream = null;
		try {
			URL url = new URL(getUrl(email));
			stream = url.openStream();
			return IOUtils.toByteArray(stream);
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new GravatarDownloadException(e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	private String formatUrlParameters() {
		List<String> params = new ArrayList<>();

		if (size != DEFAULT_SIZE)
			params.add("s=" + size);
		if (rating != DEFAULT_RATING)
			params.add("r=" + rating.getCode());
		if (defaultImage != GravatarDefaultImage.GRAVATAR_ICON)
			params.add("d=" + defaultImage.getCode());

		if (params.isEmpty())
			return "";
		else
			return "?" + StringUtils.join(params.iterator(), "&");
	}

}
