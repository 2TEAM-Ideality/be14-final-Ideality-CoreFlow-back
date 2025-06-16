package com.ideality.coreflow.notification.command.application.scheduler;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ReminderScheduler {

    public void scheduleReminderJob(Notification notification) throws SchedulerException {
        // Quartz 스케줄러 인스턴스를 생성
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 알림을 전송할 시간 (dispatch_at)
        LocalDateTime reminderTime = notification.getDispatchAt();
        //trigger는 Date 타입만 받음
        Date reminderDate = Date.from(reminderTime.atZone(ZoneId.systemDefault()).toInstant());

        // JobDetail 정의
        JobDetail job = JobBuilder.newJob(NotificationJob.class)
                .withIdentity("notificationJob" + notification.getId(), "group1")
                .usingJobData("notificationId", notification.getId())  // 알림 ID 전달
                .build();

        // Trigger 정의: 알림 전송 시간에 실행
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("notificationTrigger" + notification.getId(), "group1")
                .startAt(reminderDate)  // 알림 시간을 예약
                .build();

        // 스케줄러에 Job과 Trigger 등록
        scheduler.scheduleJob(job, trigger);

        // 스케줄러 시작
        scheduler.start();
    }

}
