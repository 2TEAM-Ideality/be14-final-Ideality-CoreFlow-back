package com.ideality.coreflow.notification.command.application.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.LocalDateTime;
import java.util.Date;

public class ReminderScheduler {

    public void scheduleReminder(Long scheduleId, Long userId, LocalDateTime reminderTime) throws SchedulerException {
        // Quartz Scheduler 인스턴스를 생성합니다.
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 알림을 전송할 시간 (reminderTime이 `dispatch_at`)
        Date reminderDate = java.sql.Timestamp.valueOf(reminderTime);

        // JobDetail 정의
        JobDetail job = JobBuilder.newJob(NotificationJob.class)
                .withIdentity("reminderJob" + scheduleId, "group1")
                .usingJobData("scheduleId", scheduleId)  // Job에 데이터를 전달
                .usingJobData("userId", userId)          // Job에 데이터를 전달
                .build();

        // Trigger 정의: 예약된 시간에 알림 전송
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("reminderTrigger" + scheduleId, "group1")
                .startAt(reminderDate)  // 알림 시간을 예약
                .build();

        // 스케줄러에 Job과 Trigger를 등록
        scheduler.scheduleJob(job, trigger);

        // 스케줄러 시작
        scheduler.start();
    }
}
