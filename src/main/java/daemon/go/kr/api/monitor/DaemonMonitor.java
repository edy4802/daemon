package daemon.go.kr.api.monitor;

import java.io.*;

public class DaemonMonitor {
    public static void main(String[] args) {
        String jarPath = "daemon.jar"; // 감시할 JAR 파일

        while (true) {
            try {
                // JAR 파일 실행
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarPath);
                processBuilder.redirectErrorStream(true); // 오류 메시지를 포함하여 출력 스트림 통합
                
                Process process = processBuilder.start();
                System.out.println("[INFO] " + jarPath + " 실행 중...");

                // 실행된 프로그램의 로그 출력 (옵션)
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                // 프로그램 종료 감지
                int exitCode = process.waitFor();
                System.out.println("[ERROR] " + jarPath + " 비정상 종료 (코드: " + exitCode + "). 5초 후 재시작...");

                // 5초 대기 후 재시작
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}