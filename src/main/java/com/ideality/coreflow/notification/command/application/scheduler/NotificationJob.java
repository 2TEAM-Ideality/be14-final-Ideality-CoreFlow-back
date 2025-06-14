package com.ideality.coreflow.notification.command.application.scheduler;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.aggregate.Status;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@RequiredArgsConstructor
public class NotificationJob implements Job {

    private final NotificationRepository notificationRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long notificationId = context.getMergedJobDataMap().getLong("notificationId");

        // 알림 데이터 가져오기
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // 알림 전송 (여기서는 예시로 콘솔 출력)
        System.out.println("알림 전송: " + notification.getContent());

        // 실제 알림 전송 로직 (예: 이메일, 푸시 알림 등)
        notification.setStatus(Status.PENDING);
        notificationRepository.save(notification); // 상태 업데이트
    }
}
