/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.uqasar.service;

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
