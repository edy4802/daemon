package daemon.go.kr.api.collection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpUtils {

	public HttpURLConnection getHttpURLConnection(String strUrl, String method) {
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(strUrl);

			conn = (HttpURLConnection) url.openConnection(); //HttpURLConnection 객체 생성
			conn.setRequestMethod(method); //Method 방식 설정. GET/POST/DELETE/PUT/HEAD/OPTIONS/TRACE
			conn.setConnectTimeout(5000); //연결제한 시간 설정. 5초 간 연결시도
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            
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
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            
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
		
		try (InputStreamReader ir = new InputStreamReader(in, "UTF-8");
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
