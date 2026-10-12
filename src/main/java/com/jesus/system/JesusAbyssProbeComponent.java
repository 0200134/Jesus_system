package com.jesus.system;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Component
public class JesusAbyssProbeComponent {

    private static final String TARGET_HOST = "127.0.0.1";
    private static final int TARGET_PORT = 8080; // 공공 레거시(eGovFrame) 바인딩 포트
    private static final int TIMEOUT_MS = 300;    // 0.3초 초고속 타임아웃 컷

    /**
     * HTTP 오버헤드를 배제하고 raw TCP 소켓 레벨에서 레거시 포트의 생사를 직접 후벼파는 프로브
     */
    @Scheduled(fixedRate = 1000)
    public void abyssSocketProbe() {
        try (Socket socket = new Socket()) {
            // 저수준 TCP 연결 시도 (300ms 안에 응답 없으면 데드락 혹은 쓰레드 뻗음으로 간주)
            socket.connect(new InetSocketAddress(TARGET_HOST, TARGET_PORT), TIMEOUT_MS);
            
            // 소켓 연결 성공 시 정상 상태 유지
        } catch (IOException e) {
            System.err.println("[☠️ ABYSS ALERT] Raw socket handshake failed! Target port " + TARGET_PORT + " is in the abyss.");
            
            // 여기서 비정상 상태 시그널을 워치독 코어와 연동하여 즉각 사출(Eject) 트리거 발동 가능
        }
    }
}
