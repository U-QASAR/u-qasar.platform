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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.wicket.markup.html.form.upload.FileUpload;

/**
 *
 *
 */
public abstract class FileUploadHelper extends FileUploadUtil implements Serializable {

	/**
	 * Performs a check against the MimeType of the uploaded file. The default
	 * implementation performs no MimeType check and always returns true.
	 *
	 * @param file the FileUpload containing a file whose mimetype is to be
	 * checked.
	 * @return <code>true</code> if the file captured in the upload corresponds
	 * to the exptected MimeType, <code>false</code> otherwise
	 */
	public boolean hasValidMimeType(FileUpload file) {
		return true;
	}

	/**
	 * The default implementation simply writes the content of the uploaded file
	 * to the target path.
	 *
	 * @param file The uploaded file to be stored in the target path.
	 * @param target The path where the uploaded file will be written to.
	 * @return The path where the uploaded file was written to.
	 * @throws IOException if any error occurs during writing of the file.
	 */
	Path processFileUpload(FileUpload file, Path target) throws IOException {
		file.writeTo(target.toFile());
		return target;
	}

	/**
	 * Processes and writes the uploaded file to the upload folder of the given
	 * user .
	 * <p>
	 * Depending on the parameter <code>overwrite</code> the original filename
	 * is kept (and thus existing files with the same name will be overwritten)
	 * OR a new filename will be generated for the uploaded file.</p>
	 * <p>
	 * In that case	that a new file name for the file uploaded will be
	 * generated, that new name will consist of the original name and a random
	 * UUID in the following structure:</p>
	 * <code>
	 * <pre>
	 *   &lt;original file name&gt;.&lt;UUID&gt;.&lt;original file extension&gt;
	 * </pre>
	 * </code>
	 *
	 * Example:
	 * <p>
	 * Original file name was "MyExcelSheet.xlsx" then the new name will
	 * be</p><p>
	 * <code>MyExcelSheet.e1823ce3-1158-4e83-bcb4-36114a1ce259.xlsx</code></p>
	 *
	 * @param file The uploaded file to be stored in the upload folder of the
	 * given user.
	 * @param user The user object used for determining the upload folder where
	 * the uploaded file will be written to.
	 * @param overwrite <code>true</code> if an existing file should be
	 * overwritten (i.e. the original filename will be kept), <code>false</code>
	 * if a new file name will be generated.
	 * @return The path where the uploaded file was written to.
	 * @throws IOException if any error occurs during writing of the file.
	 */
	private Path processUserFileUpload(FileUpload file, User user, boolean overwrite) throws IOException {
		Path target = getNewFileName(file, user, overwrite);
		processFileUpload(file, target);
		return target;
	}

	/**
	 * Processes and writes the uploaded file into the upload folder of the
	 * given user and overwrites any existing file.
	 *
	 * @param file The uploaded file to be stored in the upload folder of the
	 * givenuser.
	 * @param user The user object used for determining the upload folder where
	 * the uploaded file will be written to.
	 * @return The path where the uploaded file was written to.
	 * @throws IOException if any error occurs during writing of the file.
	 */
	public Path processUserFileUpload(FileUpload file, User user) throws IOException {
		return processUserFileUpload(file, user, true);
	}

	
	public Path processUserFileUpload(byte[] file, User user) throws IOException {		
		Path imageTargetPath = null;
		String fileName = user.getId().toString() + ".png";
		try {
			imageTargetPath = FileSystems.getDefault().getPath(getUserProfilePicturesUploadFolder().toString(), fileName); 
			System.out.println(imageTargetPath.toString());
			FileOutputStream fos = new FileOutputStream(imageTargetPath.toString());
			fos.write(file);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		return imageTargetPath;
	}
}
