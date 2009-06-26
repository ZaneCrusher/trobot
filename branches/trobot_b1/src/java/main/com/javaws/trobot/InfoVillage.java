/*
 * ~ TROBOT ~
 * 
 * $Id$
 */

package com.javaws.trobot;

import java.text.NumberFormat;

/**
 * 
 * @author jhsea3do
 * 
 */
public class InfoVillage extends InfoKarte {

	/**
	 * resource types
	 */
	final public static String[] RS_TYPES = { "WOOD", "CLAY", "IRON", "CROP" };

	/**
	 * village name
	 */
	private String name;

	/**
	 * resource upper limits
	 */
	private int[] rsLimits = new int[RS_TYPES.length];

	/**
	 * resource actual counts
	 */
	private int[] rsActuals = new int[RS_TYPES.length];

	/**
	 * resource increase counts
	 */
	private int[] rsIncreases = new int[RS_TYPES.length];

	/**
	 * @return the name
	 */
	public String getName() {

		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return the rsActuals
	 */
	public int[] getRsActuals() {

		return rsActuals;
	}

	/**
	 * @return the rsIncreases
	 */
	public int[] getRsIncreases() {

		return rsIncreases;
	}

	/**
	 * @return the rsLimits
	 */
	public int[] getRsLimits() {

		return rsLimits;
	}

	/**
	 * @return the rsActual
	 */
	public int getRsActual(int type) {

		return rsActuals[type];
	}

	/**
	 * @return the rsIncrease
	 */
	public int getRsIncrease(int type) {

		return rsIncreases[type];
	}

	/**
	 * @return the rsLimit
	 */
	public int getRsLimit(int type) {

		return rsLimits[type];
	}

	/**
	 * @return the rsActual
	 */
	public void setRsActual(int type, int value) {

		rsActuals[type] = value;
	}

	/**
	 * @return the rsIncrease
	 */
	public void setRsIncrease(int type, int value) {

		rsIncreases[type] = value;
	}

	/**
	 * @return the rsLimit
	 */
	public void setRsLimit(int type, int value) {

		rsLimits[type] = value;
	}

	/**
	 * @return the rsActual
	 */
	public void setRsActual(int type, String value) {

		setRsActual(type, Integer.parseInt(value));
	}

	/**
	 * @return the rsIncrease
	 */
	public void setRsIncrease(int type, String value) {

		setRsIncrease(type, Integer.parseInt(value));
	}

	/**
	 * @return the rsLimit
	 */
	public void setRsLimit(int type, String value) {

		setRsLimit(type, Integer.parseInt(value));
	}

	/**
	 * @param type
	 * @return the rsRate
	 */
	public double getRsRate(int type) throws ArithmeticException {

		double actual = (double) getRsActual(type);
		double limit = (double) getRsLimit(type);
		return actual / limit;
	}

	/**
	 * @param type
	 * @return the rsRate
	 */
	public double getRsTime(int type) throws ArithmeticException {

		double actual = (double) getRsActual(type);
		double limit = (double) getRsLimit(type);
		double increase = (double) getRsIncrease(type);
		double time = (increase > 0) ? (limit - actual) / increase
				: (actual / increase);
		return time;
	}

	/**
	 * @param type
	 * @return the rsRate string
	 */
	public String getRsRateString(int type) {

		String value = null;
		try {
			value = NumberFormat.getInstance().format(this.getRsRate(type));
		} catch (ArithmeticException aex) {
			value = "infinity";
		}
		return value;
	}

	/**
	 * @param type
	 * @return the rsRate string
	 */
	public String getRsTimeString(int type) {

		String value = null;
		try {
			value = NumberFormat.getInstance().format(this.getRsTime(type));
		} catch (ArithmeticException aex) {
			value = "infinity";
		}
		return value;
	}

	/**
	 * copy properties from another village
	 * 
	 * @param village
	 */
	public void copy(InfoVillage village) {

		for (int i = 0; i < this.rsActuals.length; i++) {
			if (null != village.rsActuals) {
				this.rsActuals[i] = village.rsActuals[i];
			}
			if (null != village.rsIncreases) {
				this.rsIncreases[i] = village.rsIncreases[i];
			}
			if (null != village.rsLimits) {
				this.rsLimits[i] = village.rsLimits[i];
			}
		}

		if (null != village.name) {
			this.name = village.name;
		}

		if (null != village.getD()) {
			this.setD(village.getD());
		}

		if (null != village.getC()) {
			this.setC(village.getC());
		}

		if (null != village.getPosX()) {
			this.setPosX(village.getPosX());
		}

		if (null != village.getPosY()) {
			this.setPosY(village.getPosY());
		}

		if (null != village.getType()) {
			this.setType(village.getType());
		}

	}

}
