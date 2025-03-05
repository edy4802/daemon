package daemon.go.kr.api.collection;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import daemon.go.kr.api.config.daemonProperties;

public class ApiCollectionDaemon implements Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		HttpUtils hUtils = new HttpUtils();
		ExcelUtils excelUtils = new ExcelUtils();
		daemonProperties daemonProperties = new daemonProperties();
		Map<String, Object> propsMap = daemonProperties.getProperties();
		System.out.println("propsMap : " + propsMap);
		
		String url = null;
		String method = null;
		
		JSONParser parser = new JSONParser(); // JSON 파일 읽기
		Reader reader = null;
		String inFilePath = "";
		String inFileName = "";
		
		if(StringUtils.isNotEmpty((String)propsMap.get("filePath"))) {
			inFilePath = (String) propsMap.get("filePath");
		}
		
		LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDateTime = currentTime.format(formatter);
        inFileName = formattedDateTime + "_KOSIS데이터.json";
		
		try {
			reader = new FileReader(inFilePath + inFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		// 특정한 위치에 있는 JSON 파일을 읽는다
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) parser.parse(reader);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		JSONArray jArray = (JSONArray) jsonObject.get("input"); // 파라미터(입력항목)
		JSONObject sourceObj = (JSONObject) jsonObject.get("source"); // 소스정보
		
		if(StringUtils.isNotEmpty((String)sourceObj.get("SRC_VAL"))) {
			url = (String) sourceObj.get("SRC_VAL"); // URL정보를 가져온다
			
			if(StringUtils.isNotEmpty((String)sourceObj.get("METHOD"))) {
				if(sourceObj.get("METHOD").equals("GET")) {
					url += "?";
					method = "GET";
				} else if(sourceObj.get("METHOD").equals("POST")) {
					method = "POST";
				}
			}
		}
		
		for(int i = 0; i < jArray.size(); i++) {
			JSONObject obj = (JSONObject) jArray.get(i);
			if(StringUtils.isNotEmpty((String)obj.get("INP_TYPE")) &&obj.get("INP_TYPE").equals("URL")) {
				url += "&" + obj.get("INP_KEY") + "=" + obj.get("INP_VAL");
			}
		}

		String result = "";
		HttpURLConnection conn = null;
		
        //HttpURLConnection 객체 생성
		if(url.indexOf("https") >= 0) conn = hUtils.getHttpsURLConnection(url, method);
		else conn = hUtils.getHttpURLConnection(url, method);
		
		//URL 연결에서 데이터를 읽을지에 대한 설정 ( defualt true )
		//API에서 받은 데이터를 StringBuilder 형태로 리턴하여 줍니다. 
        result = hUtils.getHttpRespons(conn);
        result = result.replaceAll("(?<=[{,\\s])([A-Za-z0-9_]+)(?=\\s*:)", "\"$1\"");
        
        JSONParser objParser = new JSONParser(); 
        JSONArray resultArray = null;
		try {
			resultArray = (JSONArray) objParser.parse(result);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// 엑셀 파일로 만들기
		excelUtils.makeExcel(resultArray);
		
        // .JSON 형식의 파일을 생성한다
        String outfilePath = "C:\\Users\\INIT_PC\\Desktop\\";
        String outfileName = "";
        
        LocalDateTime currentTime2 = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime2 = currentTime2.format(formatter2);
        outfileName = formattedDateTime2 + ".json";
        
        FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(outfilePath + outfileName);
			fileWriter.write(resultArray.toJSONString());
	        fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
