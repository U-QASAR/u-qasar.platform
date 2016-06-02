package eu.uqasar.test;

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

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 *
 */

/**
 *
 *
 */
public abstract class ArquillianBaseTest {

	protected static final String REST_BASE_URL = "http://localhost:8080/foobar9-test";

	@Deployment(managed = true)
	public static WebArchive createJBoss7TestArchive() {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
		String basePackage = "eu.uqasar";
		basePackage.replaceAll(".", "/");
		WebArchive webArchive = ShrinkWrap
				.create(WebArchive.class, "foobar9-test.war")
				.addPackages(true, basePackage + "/interceptor",
						basePackage + "/model", basePackage + "/qualifier",
						basePackage + "/rest", basePackage + "/service",
						basePackage + "/util")
				.addClass(ArquillianBaseTest.class)
				// add a custom datasource definition for testing as to not
				// interfere with normal deployment's database
				.addAsWebInfResource("test-ds.xml")
				.addAsWebInfResource("test-persistence.xml",
						"classes/META-INF/persistence.xml")
				.addAsWebInfResource("test-beans.xml", "beans.xml")
				.addAsLibraries(libs);
		return webArchive;
	}

}