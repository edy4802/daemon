package daemon.go.kr.api.collection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ExcelUtils {

	public void makeExcel(JSONArray resultArray) {
		String outfilePath = "C:\\Users\\INIT_PC\\Desktop\\";
        String outfileName = "";
		
		LocalDateTime currentTime2 = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime2 = currentTime2.format(formatter2);
        outfileName = formattedDateTime2 + ".xlsx";
		
		// .xlsx (엑셀) 형식의 파일을 생성한다
	    try (Workbook workbook = new XSSFWorkbook();
	    	FileOutputStream fileOut = new FileOutputStream(outfilePath + outfileName)) {

	    	Sheet sheet = workbook.createSheet("데이터");

	    	// 헤더 생성 (첫 번째 JSON 객체의 키를 사용)
	    	JSONObject firstObj = (JSONObject) resultArray.get(0);
	    	Set<String> keys = firstObj.keySet();
	    	Row headerRow = sheet.createRow(0);
	    	int colNum = 0;
	    	for (String key : keys) {
	    		headerRow.createCell(colNum++).setCellValue(key);
	    	}

	    	// 데이터 작성
	    	for (int i = 0; i < resultArray.size(); i++) {
	    		Row row = sheet.createRow(i + 1);
	    		JSONObject obj = (JSONObject) resultArray.get(i);
	    		int j = 0;
	    		for (String key : keys) {
	    			row.createCell(j++).setCellValue(obj.get(key).toString());
	    		}
	    	}

	    	// 엑셀 파일 저장
	    	workbook.write(fileOut);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
    
}
