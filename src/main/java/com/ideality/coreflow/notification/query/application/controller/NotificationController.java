package com.ideality.coreflow.notification.query.application.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
public class NotificationController {

    @GetMapping("/api/notifications/stream")
    public SseEmitter streamNotifications() {
        // SseEmitter 객체 생성 (클라이언트와의 연결을 유지하는 객체)
        SseEmitter emitter = new SseEmitter();

        // 비동기 방식으로 알림을 전송
        new Thread(() -> {
            try {
                // 10초마다 알림을 전송
                while (true) {
                    // 클라이언트에게 알림 전송
                    emitter.send(SseEmitter.event().data("새로운 알림이 도착했습니다!"));
                    TimeUnit.SECONDS.sleep(5);  // 5초마다 전송
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);  // 에러 발생 시 처리
            }
        }).start();

        // 연결을 완료하고 SseEmitter 반환
        return emitter;
    }
}
