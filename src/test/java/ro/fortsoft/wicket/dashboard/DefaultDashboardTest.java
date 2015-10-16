package ro.fortsoft.wicket.dashboard;

import java.util.ArrayList;
import java.util.Collections;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jboss.solder.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class DefaultDashboardTest extends TestCase {

	private static Logger logger = Logger.getLogger(DefaultDashboardTest.class);

	private String id;
	private String title;
	int columnCount;
	private DefaultDashboard defaultDashboard;
	private AbstractWidget widget;

	@Before
	public void setUp() {
		id = "DefaultDash";
		title  = "This is Dashboard";
		columnCount = 2;
		defaultDashboard = new DefaultDashboard(id, title);
		widget = new AbstractWidget() {
			private static final long serialVersionUID = 1L;
			@Override
			public WidgetView createView(String viewId) {
				return null;
			}
		};
		widget.setId("2343234");
		
	}

	@Test
	public void testCreateNewDashboard() {
		logger.info("Testing create new Dashboard");
		final String id = "newDash";
		final String dashName = "New Dashboard";
		DefaultDashboard newDash = new DefaultDashboard(id, dashName);
		assertNotNull(newDash);
		assertEquals(id, newDash.getDashboardId());
		assertEquals(dashName, newDash.getTitle());
	}
		
	@Test
	public void testGetId() {
		logger.info("Testing get Dashboard ID");
		assertNotNull(defaultDashboard);
		assertEquals("DefaultDash", defaultDashboard.getDashboardId());
	}

	@Test
	public void testGetTitle() {
		logger.info("Testing get Dashboard title");
		assertNotNull(defaultDashboard);
		assertEquals("This is Dashboard", defaultDashboard.getTitle());
	}

	@Test
	public void testSetColumnCount() {
		logger.info("Testing set Dashboard column count");
		final int colCount = 3;
		DefaultDashboard defaultdashboard = new DefaultDashboard("testDash", "Test Dashboard");
		assertNotNull(defaultdashboard);
		defaultdashboard.setColumnCount(colCount);
		assertEquals(colCount, defaultdashboard.getColumnCount());		
	}
	
	@Test
	public void testAddDeleteWidget() {
		logger.info("Testing add and delete widget");
		DefaultDashboard defaultdashboard = new DefaultDashboard("testDash", "Test Dashboard");
		
		defaultdashboard.addWidget(widget);
		Assert.assertEquals(widget, defaultdashboard.getWidget("2343234"));
		
		defaultdashboard.deleteWidget("2343234");
		Assert.assertEquals(null, defaultdashboard.getWidget("2343234"));
	}
	
	
	@Test
	public void testComparator() {
		logger.info("Testing WidgetComparator");
		AbstractWidget widget2 = widget;
		
		WidgetLocation wLoc = new WidgetLocation();
		wLoc.setColumn(2);
		wLoc.setRow(1);
		widget2.setLocation(wLoc);
		
		ArrayList<Widget> wList = new ArrayList<Widget>();
		wList.add(widget);
		wList.add(widget2);
		Collections.sort(wList, new WidgetComparator());
		Assert.assertEquals(widget, wList.get(0));
	
		wList.remove(widget);
		wLoc.setColumn(1);
		wLoc.setRow(3);
		widget.setLocation(wLoc);
		wList.add(widget);
		Collections.sort(wList, new WidgetComparator());
		Assert.assertEquals(widget2, wList.get(0));
	
	}
}
