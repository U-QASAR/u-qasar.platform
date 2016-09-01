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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.jboss.solder.logging.Logger;

/**
 *
 *
 */
@Getter(AccessLevel.PRIVATE)
public class FileUploadUtil {

	private static final Logger logger = Logger.getLogger(FileUploadHelper.class);
	@Getter(AccessLevel.PRIVATE)
	private static String uploadFolderBasePath;

	private static Path getUserUploadFolder(User user) throws IOException {
		String userId = "default";
		if (user != null) {
			userId = String.valueOf(user.getId());
		}
		Path path = FileSystems.getDefault().getPath(getUploadFolderBasePath(), userId);
		if (!Files.exists(path)) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path getBaseUploadFolder() throws IOException {
		Path basePath = FileSystems.getDefault().getPath(getUploadFolderBasePath());
		if (!Files.exists(basePath)) {
			Files.createDirectories(basePath);
		}
		return basePath;
	}

	static Path getUserProfilePicturesUploadFolder() throws IOException {
		Path basePath = FileSystems.getDefault().getPath(getUploadFolderBasePath(), "userprofiles");
		if (!Files.exists(basePath)) {
			Files.createDirectories(basePath);
		}
		return basePath;
	}

	private static MediaType getMimeType(InputStream stream, Metadata md) throws IOException {
		MediaType mediaType;
		try (BufferedInputStream bis = new BufferedInputStream(stream)) {
			AutoDetectParser parser = new AutoDetectParser();
			Detector detector = parser.getDetector();
			mediaType = detector.detect(bis, md);
		}
		return mediaType;
	}

	Path getNewFileName(FileUpload file, User user, boolean overwrite) throws IOException {
		Path target;
		String uploadedFileFileName = file.getClientFileName();
		if (overwrite) {
			target = FileSystems.getDefault().getPath(getUserUploadFolder(user).toString(), uploadedFileFileName);
		} else {
			final String extensionPart = FilenameUtils.getExtension(uploadedFileFileName);
			final String extension = StringUtils.isEmpty(extensionPart) ? "" : "." + extensionPart;
			// we add a dot (".") before the UUID, so +1
			final int UUID_LENGTH = 1 + 36;
			// extension length is given out of extension length + the dot (".txt" == 4, no extension == 0)
			final int EXTENSION_LENGTH = extension.length();
			// MAX file name length is 255 - UUID part - Extension part
			final int MAX_BASE_FILENAME_LENGTH = 255 - UUID_LENGTH - EXTENSION_LENGTH;
			// shorten file name to no longer than max length, calculated above
			final String fileNameWithoutExtension = StringUtils.left(FilenameUtils.getBaseName(uploadedFileFileName), MAX_BASE_FILENAME_LENGTH);
			// generate new file name out of shortened base name + "." + UUID + any extension (including ".")
			final String targetFileName = String.format("%s.%s%s", fileNameWithoutExtension, UUID.randomUUID().toString(), extension);
			target = FileSystems.getDefault().getPath(getUserUploadFolder(user).toString(), targetFileName);
		}
		return target;
	}

	public static MediaType getMimeType(FileUpload upload) throws IOException {
		return getMimeType(upload.getInputStream());
	}

	private static MediaType getMimeType(InputStream stream) throws IOException {
		return getMimeType(stream, new Metadata());
	}

	public static MediaType getMimeType(File file) throws IOException {
		Metadata md = new Metadata();
		md.add(Metadata.RESOURCE_NAME_KEY, file.getAbsolutePath());
		return getMimeType(new FileInputStream(file), md);
	}
}
