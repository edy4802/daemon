package daemon.go.kr.api.collection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ApiCollectionDaemon {

	public static void main(String[] args) throws Exception {
		HttpUtils hUtils = new HttpUtils();
		
		String url = null;
		String method = null;
		
		JSONParser parser = new JSONParser(); // JSON 파일 읽기
		Reader reader = new FileReader("C:\\Users\\INIT_PC\\Desktop\\20250214_KOSIS데이터.json"); // 특정한 위치에 있는 JSON 파일을 읽는다
		JSONObject jsonObject = (JSONObject) parser.parse(reader);
		
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
        result = result.replace("{", "{\"").replace(",", ",\"").replace(":", "\":");
        result = result.replace(",\"{", ",{");
        
        JSONParser objParser = new JSONParser(); 
        JSONArray resultArray = (JSONArray) objParser.parse(result);
        
        // .JSON 형식의 파일을 생성한다
        FileWriter fileWriter = new FileWriter("C:\\Users\\INIT_PC\\Desktop\\outuut_test.json");
        fileWriter.write(resultArray.toJSONString());
        fileWriter.flush();

	}

}

class HttpUtils {
	
	public HttpURLConnection getHttpURLConnection(String strUrl, String method) {
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(strUrl);

			conn = (HttpURLConnection) url.openConnection(); //HttpURLConnection 객체 생성
			conn.setRequestMethod(method); //Method 방식 설정. GET/POST/DELETE/PUT/HEAD/OPTIONS/TRACE
			conn.setConnectTimeout(5000); //연결제한 시간 설정. 5초 간 연결시도
			conn.setRequestProperty("Content-Type", "application/json");
            
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	// https
	public HttpsURLConnection getHttpsURLConnection(String strUrl, String method) {
		URL url;
		HttpsURLConnection conn = null;
		try {
			url = new URL(strUrl);

			conn = (HttpsURLConnection) url.openConnection(); //HttpURLConnection 객체 생성
			conn.setRequestMethod(method); //Method 방식 설정. GET/POST/DELETE/PUT/HEAD/OPTIONS/TRACE
			conn.setConnectTimeout(5000); //연결제한 시간 설정. 5초 간 연결시도
			conn.setRequestProperty("Content-Type", "application/json");
            
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public String getHttpRespons(HttpURLConnection conn) {
		StringBuilder sb = null;

		try {
			if(conn.getResponseCode() == 200) {
            // 정상적으로 데이터를 받았을 경우
            	//데이터 가져오기
				System.out.println(conn.getResponseCode());
				sb = readResopnseData(conn.getInputStream());
			}else{
            // 정상적으로 데이터를 받지 못했을 경우
            
            	//오류코드, 오류 메시지 표출
				System.out.println(conn.getResponseCode());
				System.out.println(conn.getResponseMessage());
				//오류정보 가져오기
				sb = readResopnseData(conn.getErrorStream());
				System.out.println("error : " + sb.toString());
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			conn.disconnect(); //연결 해제
		};
		if(sb == null) return null;
        
		return sb.toString();
	}
	
	public StringBuilder readResopnseData(InputStream in) {
		if(in == null ) return null;

		StringBuilder sb = new StringBuilder();
		String line = "";
		
		try (InputStreamReader ir = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(ir)){
			while( (line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}
}
