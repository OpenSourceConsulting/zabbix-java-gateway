/**
 * 
 */
package com.zabbix.gateway;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 모니터링 query file 을 로딩하고, query를 찾아준다.
 * @author Bongjin Kwon
 *
 */
public class DBCheckQuery {
	private static final Logger logger = LoggerFactory.getLogger(DBCheckQuery.class);
	
	private static final String DEFAULT_QUERY_ROOT = "/usr/sbin/zabbix_java/dbcheck";
	
	private static Map<String, String> oraQueryMap ;
	
	public static void initQueryMap(){
		
		String queryRootPath = System.getenv("DB_CHECK_ROOT");
		
		if(queryRootPath == null || queryRootPath.length() == 0){
			queryRootPath = DEFAULT_QUERY_ROOT;
		}
		
		oraQueryMap = loadQuery(queryRootPath , "/oracle");
	}
	
	public static String getQuery(String dbms, String queryKey){
		
		if(oraQueryMap == null){
			throw new RuntimeException("Query map is null.");
		}
		
		logger.debug("queryKey is {}", queryKey);
		String query = null;
		
		if("oracle".equals(dbms)){
			query = oraQueryMap.get(queryKey);
		}
		
		if(query == null){
			throw new RuntimeException("Query not found. queryKey is "+ queryKey);
		}
		
		logger.debug("query : {}", query);
		
		return query;
	}
	
	private static Map<String, String> loadQuery(String queryRootPath, String dir){
		File queryDir = new File(queryRootPath + dir);
		
		Map<String, String> queryMap = new HashMap<String, String>();
		
		if(queryDir.exists() && queryDir.isDirectory()){
			
			File[] files = queryDir.listFiles(new FilenameFilter(){

				@Override
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					return name.endsWith(".sql");
				}
				
			});
			
			for (File file : files) {
				String query = getQuery(file);
				if(query.length() > 0){
					queryMap.put(getFileName(file), query);
				}
			}
			
		}
		
		return queryMap;
	}
	
	private static String getFileName(File file){
		String fileName = file.getName();
		
		String queryKey = fileName.substring(0, fileName.indexOf(".sql"));
		
		logger.debug("queryKey is {}", queryKey);
		
		return queryKey;
	}
	
	private static String getQuery(File file){
		
		logger.info("loading {}", file.getAbsolutePath());
		
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			
			String line = null;
			boolean appended = false;
			while((line = br.readLine()) != null) {
				
				if(line.trim().length() > 0 ){
					
					if(appended){
						sb.append("\n");
					}
					sb.append(line.trim());
					
					appended = true;
				}
			}
			
			String query = sb.toString();
			
			if(query.toLowerCase().startsWith("select")){
				return query;
			}else if(query.length() > 0){
				logger.warn("Invalid Query : {}", query);
			}
			
		}catch(IOException e){
			logger.error("getQuery error:", e);
		}finally{
			if( br != null){
				try{ br.close(); }catch(IOException e){}
			}
		}
		
		
		return "";
	}
}
