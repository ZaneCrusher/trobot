/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import org.junit.Before;
import org.junit.Test;

public class TrobotTest {

	private static Trobot trobot;

	@Before
	public void reset() {
		trobot = new Trobot();
	}

	@Test
	public void testLogin() throws Exception {
		// assert trobot.login() == true;
		// Assert.assertTrue(trobot.login());
	}
	
	@Test
	public void testAllianz() throws Exception {
		// assert trobot.login() == true;
		trobot.allianz(36);
	}
	
	@Test
	public void testSpieler() throws Exception {
		// assert trobot.login() == true;
		trobot.spieler("/spieler.php?uid=6739");
	}
	
	
	@Test
	public void testAll() throws Exception {
		 testAllianz();
		// testSpieler();
	}
}
