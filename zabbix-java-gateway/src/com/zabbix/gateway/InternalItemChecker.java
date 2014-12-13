/*
** Zabbix
** Copyright (C) 2001-2014 Zabbix SIA
**
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/

package com.zabbix.gateway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InternalItemChecker extends ItemChecker
{
	private static final Logger logger = LoggerFactory.getLogger(InternalItemChecker.class);

	public InternalItemChecker(JSONObject request) throws ZabbixException
	{
		super(request);
	}

	@Override
	protected String getStringValue(String key) throws Exception
	{
		ZabbixItem item = new ZabbixItem(key);

		if (item.getKeyId().equals("zabbix"))
		{
			if (3 != item.getArgumentCount() ||
					!item.getArgument(1).equals("java") )
				throw new ZabbixException("required key format: zabbix[java,<jdbcUrl>,<parameter>]");

			String jdbcUrl = item.getArgument(2);
			String parameter = item.getArgument(3);

			if (parameter.equals("version"))
				return GeneralInformation.VERSION;
			else if (parameter.equals("ping"))
				return "1";
			else if (jdbcUrl != null && jdbcUrl.length() > 0)
				return getDbData(jdbcUrl, parameter);
			else
				throw new ZabbixException("third parameter '%s' is not supported", parameter);
		}
		else
			throw new ZabbixException("key ID '%s' is not supported", item.getKeyId());
	}
	
	private String getDbData(String jdbcUrl, String queryKey){
		
		//String DB_URL = "jdbc:oracle:thin:@192.168.0.150:1521:oscOra";
		String DB_URL = jdbcUrl;
		String DB_USER = "ZABBIX";
		String DB_PASSWORD = "ZABBIX";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String value = "";

		//String query = "select count(*) from v$session where TYPE!= 'BACKGROUND' and status='ACTIVE'";
		String query = DBCheckQuery.getQuery("oracle", queryKey);
		try {
			// 드라이버를 로딩한다.
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e ) {
			e.printStackTrace();
		}

		try {
			// 데이터베이스의 연결을 설정한다.
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

			// Statement를 가져온다.
			stmt = conn.createStatement();

			// SQL문을 실행한다.
			rs = stmt.executeQuery(query);

			while (rs.next()) { 
				value = rs.getString(1);
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			try {
				// ResultSet를 닫는다.
				rs.close();
				// Statement를 닫는다.
				stmt.close();
				// Connection를 닫는다.
				conn.close();
			} catch ( SQLException e ) {}
		}
		
		return value;
	}
}
