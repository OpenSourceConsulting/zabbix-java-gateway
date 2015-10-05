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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServerConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 
 * </pre>
 * @author Bong-Jin Kwon
 */
public class MBeanServerConnectionHolder {
	
	private static final Logger logger = LoggerFactory.getLogger(MBeanServerConnectionHolder.class);
	
	/**
	 * standard MBeanServerConnectionModel key
	 */
	public static final String STRD_MBSC = "1";
	
	/**
	 * weblogic MBeanServerConnectionModel key
	 */
	public static final String WL_MBSC = "2";

	private static ThreadLocal<Map<String, MBeanServerConnectionModel>> local = new ThreadLocal<Map<String, MBeanServerConnectionModel>>();
	
	
	private MBeanServerConnectionHolder() {
	}
	
	public static void put(String key, MBeanServerConnectionModel mbscModel) {
		Map<String, MBeanServerConnectionModel> mbscMap = getThreadLocalVarObj();
		
		if (mbscMap.containsKey(key)) {
			mbscMap.get(key).close();
		}
		
		mbscMap.put(key, mbscModel);
		logger.debug("put : {}", key);
	}
	
	public static MBeanServerConnection getMBeanServerConnection(String key){
		Map<String, MBeanServerConnectionModel> mbscMap = getThreadLocalVarObj();
		
		MBeanServerConnectionModel mbscModel = mbscMap.get(key);
		
		if (mbscModel == null) {
			return null;
		}
		
		return mbscModel.getMbsc();
	}
	
	private static Map<String, MBeanServerConnectionModel> getThreadLocalVarObj() {
		Map<String, MBeanServerConnectionModel> mbscMap = local.get();
		
		if (mbscMap == null) {
			mbscMap = new HashMap<String, MBeanServerConnectionModel>();
			local.set(mbscMap);
		}
		
		return mbscMap;
	}
	
	public static void clear() {
		Map<String, MBeanServerConnectionModel> mbscMap = local.get();
		
		if (mbscMap != null) {
			for (Entry<String, MBeanServerConnectionModel> entry : mbscMap.entrySet()) {
				entry.getValue().close();
			}
			
			mbscMap.clear();
		}
		local.remove();
		logger.debug("cleared!!");
	}
	

}
//end of MBeanServerConnectionFactory.java