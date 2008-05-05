/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TrobotTest extends junit.framework.TestCase {

	private static Trobot trobot;

	@Before
	protected void setUp() throws Exception {

		super.setUp();
		this.reset();
	}

	public void reset() throws Exception {

		Configuration config = Configuration.getConfiguration();
		String driver = config.getString("trobot.driver");
		System.out.println("reset trobot: " + driver);
		trobot = (Trobot) Class.forName(driver).newInstance();
	}

	@Test
	public void testLogin() throws Exception {

		Assert.assertTrue(trobot.login());
	}

	// public void testAllianz() throws Exception {
	//
	// Assert.assertNotNull(trobot.allianz(36));
	// }
	//
	// public void testSpieler() throws Exception {
	//
	// Assert.assertNotNull(trobot.spieler("/spieler.php?uid=6739"));
	// }
	//
	// public void testBadSpieler() throws Exception {
	//
	// try {
	// trobot.spieler("/spiele.php?uid=6739");
	// } catch (Exception ex) {
	// Assert.assertNotNull(ex);
	// }
	// }

	@After
	protected void tearDown() throws Exception {

		super.tearDown();
	}

}
