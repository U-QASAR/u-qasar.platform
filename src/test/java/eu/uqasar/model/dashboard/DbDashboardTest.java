package eu.uqasar.model.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.WidgetLocation;
import eu.uqasar.web.dashboard.widget.sonarqualitywidget.SonarQualityWidget;
import eu.uqasar.web.dashboard.widget.testlinkwidget.TestLinkWidget;
import eu.uqasar.web.dashboard.widget.widgetforjira.WidgetForJira;

public class DbDashboardTest {

	private DbDashboard dash;
	private List<Widget> widgets = new ArrayList<>();
	private Widget wdgt;

	
	@Before
	public void setUp() throws Exception {
		dash = new DbDashboard();
		dash.setId(1l);
		dash.setTitle("Test Dash");
		dash.setColumnCount(1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSharedBy() {
		Assert.assertNull(dash.getSharedBy());
	}

	@Test
	public void testSetSharedBy() {
		Assert.assertNull(dash.getSharedBy());
		String sharedBy = "Platform User";
		dash.setSharedBy(sharedBy);
		Assert.assertEquals(sharedBy, dash.getSharedBy());
	}

	@Test
	public void testGetDashboardId() {
		Long id = 1l;
		Assert.assertEquals(id, dash.getId());
	}

	@Test
	public void testGetTitle() {
		String title = "Test Dash";
		Assert.assertEquals(title, dash.getTitle());
	}

	@Test
	public void testSetTitle() {
		dash.setTitle("New Title");
		Assert.assertEquals("New Title", dash.getTitle());
	}

	@Test
	public void testGetColumnCount() {
		final int COUNT = 1;
		Assert.assertEquals(COUNT, dash.getColumnCount());
	}

	@Test
	public void testSetColumnCount() {
		int cols = 3;
		dash.setColumnCount(cols);
		Assert.assertEquals(cols, dash.getColumnCount());
	}

	@Test
	public void testGetWidgets() {
		widgets.clear();
		wdgt = new WidgetForJira();
		wdgt.setId("999");
		wdgt.setLocation(new WidgetLocation(0, 0));
		widgets.add(wdgt);
		dash.setWidgets(widgets);
		Assert.assertFalse(dash.getWidgets().isEmpty());
		Assert.assertEquals(widgets, dash.getWidgets());
	}

	@Test
	public void testGetWidget() {
		wdgt = new WidgetForJira();
		wdgt.setId("999");
		wdgt.setLocation(new WidgetLocation(0, 0));
		widgets.add(wdgt);
		dash.setWidgets(widgets);
		Assert.assertEquals(wdgt, dash.getWidget("999"));
	}

	@Test
	public void testSetWidgets() {
		List<Widget> widgets = new ArrayList<>();
		Widget wdgt = new TestLinkWidget();
		wdgt.setId("1000");
		wdgt.setLocation(new WidgetLocation(0, 0));
		widgets.add(wdgt);
		dash.setWidgets(widgets);
		
		Assert.assertNotNull(dash.getWidgets());
		Assert.assertEquals(widgets.size(), dash.getWidgets().size());		
	}

	@Test
	public void testAddWidget() {
		wdgt = new WidgetForJira();
		wdgt.setId("999");
		wdgt.setLocation(new WidgetLocation(0, 0));
		widgets.add(wdgt);
		dash.setWidgets(widgets);
		int widgetCount = 1;
		Assert.assertEquals(widgetCount, dash.getWidgets().size());
		Widget wdgt = new SonarQualityWidget();
		wdgt.setId("1001");
		wdgt.setLocation(new WidgetLocation(0, 1));
		dash.addWidget(wdgt);
		int widgetCountAfterAddition = 2;
		Assert.assertEquals(widgetCountAfterAddition, dash.getWidgets().size());		
	}

	@Test
	public void testDeleteWidget() {
		wdgt = new WidgetForJira();
		wdgt.setId("999");
		wdgt.setLocation(new WidgetLocation(0, 0));
		widgets.add(wdgt);
		dash.setWidgets(widgets);
		Assert.assertEquals(1, dash.getWidgets().size());		
		dash.deleteWidget("999");
		Assert.assertEquals(0, dash.getWidgets().size());
	}
}
