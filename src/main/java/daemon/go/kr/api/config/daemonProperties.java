package daemon.go.kr.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class daemonProperties {

	public Map<String, Object> getProperties() {
		Map<String, Object> propsMap = new HashMap<String, Object>();
		Properties props = new Properties();
		
		try {
			InputStream reader = getClass().getResourceAsStream("/props/daemon.properties");
	        props.load(reader);
	        propsMap.put("inFilePath", props.getProperty("inFilePath"));
	        propsMap.put("outFilePath", props.getProperty("outFilePath"));
	        propsMap.put("OPM", props.getProperty("OPM"));
	        propsMap.put("OPY", props.getProperty("OPY"));
	        propsMap.put("OPTM", props.getProperty("OPTM"));
	        propsMap.put("OPOM", props.getProperty("OPOM"));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		return propsMap;
	}
	
}
