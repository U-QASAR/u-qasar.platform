/*
 */
package eu.uqasar.sandbox;

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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import org.apache.wicket.util.file.File;

/**
 *
 *
 */
public class PropToXmlConverter {

	public static void main(String[] args) throws IOException {

		String pattern = "*.properties";

		Finder finder = new Finder(pattern);
		Files.walkFileTree(Paths.get("C:\\temp\\U-QASAR\\src\\main\\java\\eu"), finder);
		finder.done();

	}

	private static class Finder extends SimpleFileVisitor<Path> {

		private final PathMatcher matcher;
		private int numMatches = 0;

		public Finder(final String pattern) {
			matcher = FileSystems.getDefault()
					.getPathMatcher("glob:" + pattern);
		}

		// Compares the glob pattern against
		// the file or directory name.
		void find(Path file) {
			Path name = file.getFileName();
			if (name != null && matcher.matches(name)) {
				numMatches++;
				System.out.println(file);
//				renameXmlToPropertiesXML(file);
//				try {
//					fromPropsToXml(file);
//					fromXmlToProps(file);
					file.toFile().delete();
//				} catch (IOException ex) {
//					Logger.getLogger(PropToXmlConverter.class.getName()).log(Level.SEVERE, null, ex);
//				}
			}
		}

		void renameXmlToPropertiesXML(Path file) {
			final String newFileName = file.toString().replace(".xml", ".properties.xml");
			file.toFile().renameTo(new File(newFileName));
		}
		
		void fromPropsToXml(Path file) throws FileNotFoundException, IOException {
			Properties props = new Properties();
			OutputStream os;
			try (InputStream is = new FileInputStream(file.toFile())) {
				props.load(is);
				final String newFileName = file.toString().replace(".properties", ".xml");
				os = new FileOutputStream(newFileName);
				props.storeToXML(os, null);
			}
			os.close();
		}

		void fromXmlToProps(Path file) throws FileNotFoundException, IOException {
			Properties props = new Properties();
			OutputStream os;
			try (InputStream is = new FileInputStream(file.toFile())) {
				props.loadFromXML(is);
				final String newFileName = file.toString().replace(".xml", ".properties");
				os = new FileOutputStream(newFileName);
				props.store(os, null);
			}
			os.close();
		}

		// Prints the total number of
		// matches to standard out.
		void done() {
			System.out.println("Matched: "
					+ numMatches);
		}

		// Invoke the pattern matching
		// method on each file.
		@Override
		public FileVisitResult visitFile(Path file,
				BasicFileAttributes attrs) {
			find(file);
			return CONTINUE;
		}

		// Invoke the pattern matching
		// method on each directory.
		@Override
		public FileVisitResult preVisitDirectory(Path dir,
				BasicFileAttributes attrs) {
			find(dir);
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file,
				IOException exc) {
			System.err.println(exc);
			return CONTINUE;
		}
	}
}
