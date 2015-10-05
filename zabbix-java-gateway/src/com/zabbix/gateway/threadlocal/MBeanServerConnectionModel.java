/* Copyright (C) 2015~ Hyundai Heavy Industries. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Hyundai Heavy Industries
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with Hyundai Heavy Industries.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * Bong-jin Kwon	2015. 10. 5.				First Draft.
 */
package com.zabbix.gateway.threadlocal;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

/**
 * <pre>
 * 
 * </pre>
 * @author Bong-Jin Kwon
 */
public class MBeanServerConnectionModel {
	
	private JMXConnector jmxc;
	private MBeanServerConnection mbsc;

	/**
	 * <pre>
	 * create MBeanServerConnectionModel instance
	 * </pre>
	 */
	public MBeanServerConnectionModel(JMXConnector jmxc, MBeanServerConnection mbsc) {
		this.jmxc = jmxc;
		this.mbsc = mbsc;
	}

	public JMXConnector getJmxc() {
		return jmxc;
	}

	public void setJmxc(JMXConnector jmxc) {
		this.jmxc = jmxc;
	}

	public MBeanServerConnection getMbsc() {
		return mbsc;
	}

	public void setMbsc(MBeanServerConnection mbsc) {
		this.mbsc = mbsc;
	}
	
	public void close() {
		if (null != jmxc) {
			
			try {
				jmxc.close();
			} catch (IOException e) {
				// ignore.
			}
			
			jmxc = null;
			mbsc = null;
		}
	}

}
//end of MBeanServerConnectionModel.java