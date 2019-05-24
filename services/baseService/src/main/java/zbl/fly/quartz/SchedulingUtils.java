package zbl.fly.quartz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.transaction.annotation.Transactional;
import zbl.fly.base.quartz.CronTask;
import zbl.fly.base.quartz.Task;
import zbl.fly.base.quartz.TimedTask;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Slf4j
public class SchedulingUtils {
    @Inject
    private Scheduler scheduler;
    @Inject
    private ObjectMapper objectMapper;

    @Transactional
    public void cancelTimedTaskSchedule(TimedTask job) {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        try {
            scheduler.unscheduleJob(triggerKey);
        } catch (SchedulerException e) {
            log.error("取消任务失败", e);
        }
    }

    @Transactional
    public void addTimedTaskSchedule(TimedTask job) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            Trigger trigger = scheduler.getTrigger(triggerKey);
            JobDetail jobDetail = JobBuilder.newJob(MyJobDetail.class).withIdentity(job.getJobName(), job.getJobGroup()).build();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.put("classname", job.getClass().getTypeName());
            jobDataMap.put("timedTask", getTaskJson(job));
            //不存在创建一个
            if (null == trigger) {
                trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startAt(job.getStartTime()).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                scheduler.unscheduleJob(triggerKey);
                this.addTimedTaskSchedule(job);
            }
        } catch (SchedulerException | JsonProcessingException e) {
            log.error("安排任务失败", e);
        }
    }

    private String getTaskJson(Task job) throws JsonProcessingException {
        return objectMapper.writeValueAsString(job);
    }

    public void addCronTaskSchedule(CronTask job) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());

            Trigger trigger = scheduler.getTrigger(triggerKey);

            //不存在，创建一个
            if (null == trigger) {
                JobDetail jobDetail = JobBuilder.newJob(MyJobDetail.class)
                        .withIdentity(job.getJobName(), job.getJobGroup()).build();
                JobDataMap jobDataMap = jobDetail.getJobDataMap();
                jobDataMap.put("classname", job.getClass().getTypeName());
                jobDataMap.put("cronTask", getTaskJson(job));
                //按 定时时间 构建trigger

                trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(CronScheduleBuilder.cronSchedule(job.getCondExpession())).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                scheduler.unscheduleJob(triggerKey);
                addCronTaskSchedule(job);
            }
        } catch (SchedulerException | JsonProcessingException e) {
            log.debug("安排任务失败", e);
        }
    }
}
