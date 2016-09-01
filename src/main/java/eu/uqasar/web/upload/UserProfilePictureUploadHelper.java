/*
 */
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


import eu.uqasar.model.user.User;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.apache.tika.mime.MediaType;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.imgscalr.Scalr;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
public class UserProfilePictureUploadHelper extends FileUploadHelper {

	private static final Logger logger = Logger.getLogger(FileUploadHelper.class);
	private static final String PROFILEPIC_EXTENSION = "png";

	public void removeUserPictures(final User user) {
		try {
			Path path = getUserPicturePath(user);
            if(path != null) {
                Files.deleteIfExists(path);
            }
		} catch (IOException ex) {
			logger.warn(ex.getMessage(), ex);
		}
	}

	@Override
	public boolean hasValidMimeType(FileUpload file) {
		MediaType type = MediaType.parse(file.getContentType());
		if (!"image".equalsIgnoreCase(type.getType())) {
			return false;
		}
		boolean gif = "gif".equalsIgnoreCase(type.getSubtype());
		boolean png = "png".equalsIgnoreCase(type.getSubtype());
		boolean jpg = "jpeg".equalsIgnoreCase(type.getSubtype());
		return gif || png || jpg;
	}

	@Override
	protected Path getNewFileName(FileUpload file, User user, boolean overwrite) throws IOException {
		return getNewFileName(user);
	}

	public static Path getNewFileName(User user) throws IOException {
		return getNewFileName(user.getId());
	}

	private static Path getNewFileName(Long userId) throws IOException {
		/*	we want the user picture files to always reside in a different folder than the users 
		 personal folder, so we overwrite the method for determining the new name 
		 */
		final String basePath = getUserProfilePicturesUploadFolder().toString();
		// we always name it PNG even though it could be a JPG or GIF. 
		// This makes it easier for serving it later, so we don't have to are about extensions
		final String fileName = String.format("%s.%s", userId, PROFILEPIC_EXTENSION);
        return FileSystems.getDefault().getPath(basePath, fileName);
	}

	public Path processFile(InputStream is, Path target) throws IOException {
		try {
			final int maxDim = 500;
			BufferedImage thumb;
			BufferedImage im = ImageIO.read(is);
			if (im.getWidth() > maxDim || im.getHeight() > maxDim) {
				thumb = Scalr.resize(im, maxDim);
			} else {
				thumb = im;
			}
			try (FileOutputStream baos = new FileOutputStream(target.toFile())) {
				ImageIO.write(thumb, "PNG", baos);
			}
		} catch (IOException ignored) {
		}
		return target;
	}

	/**
	 * The user picture upload helper scales down any images larger than 500px
	 * before writing the content of the uploaded file to the target path.
	 *
	 * @param file The uploaded file to be stored in the target path.
	 * @param target The path where the uploaded file will be written to.
	 * @return The path where the uploaded file was written to.
	 * @throws IOException if any error occurs during writing of the file.
	 */
	@Override
	public Path processFileUpload(FileUpload file, Path target) throws IOException {
		return processFile(file.getInputStream(), target);
	}
    
    public Path processLdapUserPictureUpload(byte[] data, User user) {
        try {
            return processFile(new ByteArrayInputStream(data), getNewFileName(user));
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
	}

	public static Path getUserPicturePath(Long userId) {
		if (userId == null) return null;
		try {
			Path userPic = getNewFileName(userId);
			if (Files.exists(userPic)) {
				return userPic;
			}
		} catch (IOException ex) {
			logger.warn(ex.getMessage(), ex);
		}
		return null;
	}

	private static Path getUserPicturePath(User user) {
		return getUserPicturePath(user.getId());
	}

}
