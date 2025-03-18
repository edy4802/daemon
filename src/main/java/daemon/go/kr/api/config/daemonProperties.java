package daemon.go.kr.api.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

public class daemonProperties {
    private static final String CONFIG_FILE = "config.properties"; // JAR과 같은 폴더에 있는 파일

    public Map<String, Object> getProperties() {
        Map<String, Object> propsMap = new HashMap<>();
        Properties props = new Properties();

        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            //System.err.println("설정 파일이 없습니다: " + CONFIG_FILE);
            return propsMap; // 빈 맵 반환 (또는 기본값 추가 가능)
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
            
            // 필요한 설정값을 propsMap에 저장
            for (String key : props.stringPropertyNames()) {
                propsMap.put(key, props.getProperty(key));
            }
            
            //System.out.println("프로퍼티 로드 성공: " + propsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return propsMap;
    }

    public static void main(String[] args) {
        daemonProperties daemonProps = new daemonProperties();
        Map<String, Object> properties = daemonProps.getProperties();

        // 설정값 출력 테스트
        properties.forEach((key, value) -> System.out.println(key + " = " + value));
    }
}
