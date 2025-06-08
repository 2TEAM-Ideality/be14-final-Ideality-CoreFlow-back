package com.ideality.coreflow.notification.query.application.controller;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/api/notifications/stream")
    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter();

        // 비동기 방식으로 알림을 전송
        new Thread(() -> {
            try {
                // 여기에 알림 전송 로직을 추가
                while (true) {
                    // 알림 전송 (새로운 알림을 5초마다 전송)
                    notificationService.getLatestNotification()
                            .forEach(notification -> {
                                try {
                                    // 클라이언트에게 알림을 전송
                                    emitter.send(SseEmitter.event().data(notification.getContent()));
                                } catch (IOException e) {
                                    // 전송 에러 처리
                                    emitter.completeWithError(e);
                                }
                            });

                    TimeUnit.SECONDS.sleep(5);  // 5초마다 알림 확인
                }
            } catch (InterruptedException e) {
                // 스레드가 중단되었을 때의 처리
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

//    @PostMapping("/send")
//    public void sendNotification(@RequestParam Long userId,
//                                 @RequestParam String content,
//                                 @RequestParam Long targetId,
//                                 @RequestParam String targetType) {
//        notificationService.sendNotification(userId, content, targetId, targetType);
//    }

}
