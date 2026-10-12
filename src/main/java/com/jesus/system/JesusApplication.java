package com.jesus.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 0.5초 생체 신호 스케줄러 활성화 필수!
public class JesusApplication {
    public static void main(String[] args) {
        SpringApplication.run(JesusApplication.class, args);
        System.out.println("[✝️] J.E.S.U.S Watchdog Core is Online and Guarding Legacy Systems.");
    }
}
