package com.jesus.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 0.5초 생체 신호 스케줄러 활성화 필수!
@ConfigurationPropertiesScan // jesus.watchdog.* 설정 바인딩
public class JesusApplication {

    private static final Logger log = LoggerFactory.getLogger(JesusApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(JesusApplication.class, args);
        log.info("[✝️] J.E.S.U.S Watchdog Core is Online and Guarding Legacy Systems.");
    }
}
