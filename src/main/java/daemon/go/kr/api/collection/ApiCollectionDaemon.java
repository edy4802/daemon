package daemon.go.kr.api.collection;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

/*
	-------------------------------------------------------------------
  	최초 작성일 : 2025.02.17
  	최종 수정일 : 
  	최초 작성자 : 엄대영 책임
  	최종 수정자 : 최성은 주임, 김일규 주임

  	[ 메서드 설명 : 다중 파일명 패턴 처리 및 조회 메서드 호출, processJsonFile 호출 메서드 ]
  	
  	-------------------------------------------------------------------
*/
	
	daemonProperties daemonProperties = new daemonProperties();
	Map<String, Object> propsMap = daemonProperties.getProperties();
	
	DaemonLogger logger = DaemonLogger.getLogger();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		HttpUtils hUtils = new HttpUtils();
		ExcelUtils excelUtils = new ExcelUtils();
		
		// 파일 경로 ( 조회옹 )
		String inFilePath = (String) propsMap.get("inFilePath01");
		if (StringUtils.isEmpty(inFilePath)) {
			logger.log("파일 경로가 설정되지 않았습니다.");
		    return;
		}
		
		// 파일 날짜패턴 ( 조회용 )
		LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentTime.format(formatter);
		
        // 특정 패턴을 가진 JSON 파일 리스트 찾기
        List<File> jsonFiles;
        try {
            jsonFiles = Files.list(Paths.get(inFilePath))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().matches("^.*\\.json$"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (jsonFiles.isEmpty()) {
        	logger.log("처리할 JSON 파일이 없습니다.");
            return;
        }

        for (File file : jsonFiles) {
        	logger.log("처리 중: " + file.getName());
            processJsonFile(file, hUtils, excelUtils);
        }
	}	
	
	
	
/*
  	----------------------------------------------------
  	
  	[ 메서드 설명 : 파일 처리 및 makeExcel, saveJsonResult 호출 ]
  	
  	----------------------------------------------------
*/ 	
 	private void processJsonFile(File file, HttpUtils hUtils, ExcelUtils excelUtils) {
 		
 		JSONParser parser = new JSONParser(); // JSON 파일 읽기
 		JSONObject jsonObject;
 		
 		try (Reader reader = new FileReader(file)) {
            jsonObject = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }
 		
 		
 		String url = null;
		String method = null;
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
		
		for (Object obj : jArray) {
            JSONObject param = (JSONObject) obj;
            if (StringUtils.isNotEmpty((String) param.get("INP_TYPE")) && param.get("INP_TYPE").equals("URL")) {
                url += "&" + param.get("INP_KEY") + "=" + param.get("INP_VAL");
            }
        }
		
		
		String result = "";
		HttpURLConnection conn = null;
		
		// HttpURLConnection 객체 생성
		if(url.indexOf("https") >= 0) {
			conn = hUtils.getHttpsURLConnection(url, method);
		} else {
			conn = hUtils.getHttpURLConnection(url, method);
		}
		
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
		String outFilePath = (String)propsMap.get("outFilePath01");
		excelUtils.makeExcel(resultArray, file.getName(), outFilePath);
		
		// 결과 JSON 파일 저장
        saveJsonResult(resultArray, file.getName());
 	}

 	
/*
  	-------------------------------
  	
  	[ 메서드 설명 : 처리된 데이터 파일로 출력 ]
  	
  	-------------------------------
*/ 	
 	private void saveJsonResult(JSONArray resultArray, String originalFileName) {
 		
 		// 파일 경로 ( 출력옹 )
 		String outFilePath = (String) propsMap.get("outFilePath01");
		
		// 파일 날짜패턴 ( 출력용 )
		LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss"); // 일단 초까지 파일명으로 뽑아서 중복 안 되도록 임시로 작업
        String formattedDateTime = currentTime.format(formatter);

        // 원본 파일명에서 확장자 제거
        String baseFileName = originalFileName.replaceAll("\\.json$", "");
        
        // 출력 파일명: "yyyyMMdd_원본파일명.json"
        String outFileName = formattedDateTime + "_" + baseFileName + ".json";
        
        FileWriter fileWriter;
        
        try {
			fileWriter = new FileWriter(outFilePath + outFileName);
			fileWriter.write(resultArray.toJSONString());
	        fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
         	
}       