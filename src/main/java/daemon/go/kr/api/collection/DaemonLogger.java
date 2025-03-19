package daemon.go.kr.api.collection;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DaemonLogger {
	Logger logger = Logger.getLogger("DaemonLogger");		// 'DaemonLogger' 라는 이름으로 Logger 클래스 생성 
    private static DaemonLogger instance = new DaemonLogger();		// DaemonLogger 클래스 singleton pattern 적용 

    private FileHandler logFile = null;				
    private FileHandler warningFile = null;
    private FileHandler fineFile = null;
    
    private DaemonLogger(){						// private 생성자 
	
       try {
    	   logFile = new FileHandler("log\\log.txt", true); 		// 파일 핸들러 생성
           warningFile = new FileHandler("log\\warning.txt", true); 	// 파일 핸들러 생성
           fineFile = new FileHandler("log\\fine.txt", true);		// 파일 핸들러 생성

       } catch (SecurityException e) {			// 파일 핸들러 관련 exception
    	   e.printStackTrace();
       } catch (IOException e) {
    	   e.printStackTrace();
       }

       logFile.setFormatter(new SimpleFormatter());	// 로그를 기록하는 형식 지정 
       warningFile.setFormatter(new SimpleFormatter()); 	// 로그를 기록하는 형식 지정 
       fineFile.setFormatter(new SimpleFormatter()); 	// 로그를 기록하는 형식 지정 

       logger.setLevel(Level.ALL);				// 전체에 대한 logger의 level 지정
       fineFile.setLevel(Level.FINE);			// fineFile 핸들러의 level 지정	
       warningFile.setLevel(Level.WARNING);		// warningFile 핸들러의 level 지정	
	
       logger.addHandler(logFile);				// 핸들러 객체 등록
       logger.addHandler(warningFile);			// 핸들러 객체 등록
       logger.addHandler(fineFile);			// 핸들러 객체 등록
    }	

    public static DaemonLogger getLogger(){			// singleton pattern
    	return instance;
    }


    public void log(String msg){				// log 메서드가 불리면 
								// msg 문자열을 모든 레벨 log 파일에 기록
    	logger.finest(msg);
	    logger.finer(msg);
	    logger.fine(msg);
	    logger.config(msg);
	    logger.info(msg);
	    logger.warning(msg);
	    logger.severe(msg);
    }

    public void fine(String msg){				// fine 메서드가 불리면
    	logger.fine(msg);					// fine 레벨 이상의 로그파일에 msg 문자열 기록
    }	

    public void warning(String msg){			// warning 메서드가 불리면
    	logger.warning(msg);					// warning 레벨 이상의 로그파일에 msg 문자열 기록
    }
    
}
