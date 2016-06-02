/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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


import eu.uqasar.model.tree.Metric;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.tree.QualityIndicator;
import eu.uqasar.model.tree.QualityObjective;
import eu.uqasar.model.tree.TreeNode;
import java.io.Serializable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 *
 */
public class PersistenceTester {

	public static void main(String[] args) throws InterruptedException {

		SessionFactory sessionFactory = new Configuration()
				.addAnnotatedClass(TreeNode.class)
				.addAnnotatedClass(Project.class)
				.addAnnotatedClass(QualityObjective.class)
				.addAnnotatedClass(QualityIndicator.class)
				.addAnnotatedClass(Metric.class)
				.setProperty("hibernate.connection.url", "jdbc:h2:file:/c:/temp/uqasar;DB_CLOSE_DELAY=-1")
				.setProperty("hibernate.hbm2ddl.auto", "create")
				.setProperty("hibernate.show_sql", "false")
				.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
				.setProperty("hibernate.connection.driver_class", "org.h2.Driver")
				.buildSessionFactory();
		System.out.println("Create and save some nodes");
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();

		Project project = new Project("project1", "prj1");
		QualityObjective qo1 = new QualityObjective("qo1", project);
		QualityObjective qo2 = new QualityObjective("qo2", project);
		QualityIndicator qi11 = new QualityIndicator("qi1.1", qo1);
		QualityIndicator qi12 = new QualityIndicator("qi1.2", qo1);
		QualityIndicator qi13 = new QualityIndicator("qi1.3", qo1);
		QualityIndicator qi21 = new QualityIndicator("qi2.1", qo2);
		QualityIndicator qi22 = new QualityIndicator("qi2.2", qo2);

		Metric m111 = new Metric("metric1.1.1", qi11);
		Metric m112 = new Metric("metric1.1.2", qi11);
		Metric m113 = new Metric("metric1.1.3", qi11);
		Metric m121 = new Metric("metric1.2.1", qi12);
		Metric m122 = new Metric("metric1.2.2", qi12);
		Metric m131 = new Metric("metric1.3.1", qi13);
		Metric m211 = new Metric("metric2.1.1", qi21);
		Metric m221 = new Metric("metric2.2.1", qi22);

		Serializable projectId = session.save(project);
		session.save(qo1);

		session.save(qi11);
		session.save(qi12);
		session.save(qi13);
		session.save(qi21);
		session.save(qi22);

		session.save(m111);
		session.save(m112);
		session.save(m113);

		session.save(m121);
		System.out.println(m121.getParent().getChildren());
		m121.changePositionWithPreviousSibling();
		final long m121Id = m121.getId();
		System.out.println(m121.getParent().getChildren());
		session.save(m121.getParent());

		session.save(m122);
		System.out.println(m122.getParent().getChildren());
		m122.changePositionWithNextSibling();
		final long m122Id = m122.getId();
		System.out.println(m122.getParent().getChildren());
		session.save(m122.getParent());
		
		session.save(m131);
		session.save(m211);
		session.save(m221);

		transaction.commit();
		session.close();

		System.out.println("Now load and print the nodes");
		session = sessionFactory.openSession();
//		Project prj = (Project) session.get(Project.class, projectId);	
//		for (TreeNode node : prj.getChildren()) {
//			for (TreeNode node2 : node.getChildren()) {
//				for (TreeNode node3 : node2.getChildren()) {
//					System.out.println(node3);
//					System.out.println(node3.getParent().getParent().getParent());
//					System.out.println(node3.getProject());
//				}
//			}
//		}
		session.close();
	}
}
