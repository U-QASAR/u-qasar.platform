/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.uqasar.service;

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


import java.util.Date;

import javax.inject.Inject;

import org.jboss.solder.logging.Logger;
import org.junit.Assert;

import eu.uqasar.model.notification.project.ProjectNearEndNotification;
import eu.uqasar.test.ArquillianBaseTest;

public class NotificationServiceTest extends ArquillianBaseTest {

	// inject a logger
	@Inject
	Logger logger;
	
	@Inject
	NotificationService service;
	
	//@Test
	public void testMarkAsRead() {
		logger.info("testing marking a notification as read");
		ProjectNearEndNotification projectNearEnd = new ProjectNearEndNotification();
		projectNearEnd = (ProjectNearEndNotification) service.create(projectNearEnd);
		Date beforeMarkedAsReadDate = projectNearEnd.getReadDate();
		Assert.assertNull(beforeMarkedAsReadDate);
		ProjectNearEndNotification markAsRead = service.markAsRead(projectNearEnd);
		Date afterMarkedAsReadDate = (Date) markAsRead.getReadDate().clone();
		Assert.assertNotSame(beforeMarkedAsReadDate, afterMarkedAsReadDate);
	}
	
}
