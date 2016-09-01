/*
 */
package eu.uqasar.web.components.resources;

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
import eu.uqasar.web.upload.UserProfilePictureUploadHelper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import lombok.NoArgsConstructor;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;
import org.imgscalr.Scalr;

@NoArgsConstructor
public class UserPictureResource extends DynamicImageResource {

	private int maxSize = 500;
	private Long userId;

	public static final ContextRelativeResource defaultProfilePictureRef
			= new ContextRelativeResource("assets/img/user/anonymous.png");

	private UserPictureResource(int maxSize) {
		this.maxSize = maxSize;
	}

	private UserPictureResource(long userId, int maxSize) {
		this.maxSize = maxSize;
	}

	public UserPictureResource(User user, int maxSize) {
		this.maxSize = maxSize;
		this.userId = user.getId();
	}

	private Long getUserId(Attributes attributes) {
		if (attributes.getParameters() == null) {
			return this.userId;
		}
		Long attrLong = attributes.getParameters().get("userId").toLongObject();
		if (attrLong == null) {
			return this.userId;
		} else {
			return attrLong;
		}
	}

	private int getMaxDimension(Attributes attributes) {
		if (attributes.getParameters() == null) {
			return this.maxSize;
		}
		return attributes.getParameters().get("dim").toInt(this.maxSize);
	}

	@Override
	protected byte[] getImageData(Attributes attributes) {
		Long attrUserId = getUserId(attributes);
		int attrMaxSize = getMaxDimension(attributes);
		try {
			BufferedImage im;
			BufferedImage thumb;
			Path userPicturePath = UserProfilePictureUploadHelper.getUserPicturePath(attrUserId);
			if (attrUserId != null && userPicturePath != null) {
				im = ImageIO.read(userPicturePath.toFile());
				setLastModifiedTime(Time.millis(userPicturePath.toFile().lastModified()));
			} else {
				setLastModifiedTime(Time.millis(75675677));
				im = ImageIO.read(defaultProfilePictureRef.getCacheableResourceStream().getInputStream());
			}
			if (im.getWidth() > attrMaxSize || im.getHeight() > attrMaxSize) {
				// TODO add caching, currently every request scales the image!!
				thumb = Scalr.resize(im, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, attrMaxSize);
			} else {
				thumb = im;
			}
			byte[] bytes;
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(thumb, "PNG", baos);
				bytes = baos.toByteArray();
			}
			return bytes;
		} catch (IOException | ResourceStreamNotFoundException ignored) {
		}
		return null;
	}

	public static ResourceReference asReference() {
		return new ResourceReference(UserPictureResource.class.getCanonicalName()) {
			@Override
			public IResource getResource() {
				return new UserPictureResource();
			}
		};
	}
}
