package daemon.go.kr.api.collection;

import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import daemon.go.kr.api.config.daemonProperties;

public class mainEntry {
	
	public static void main(String[] args) {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		daemonProperties daemonProps = new daemonProperties();
        Map<String, Object> propsMap = daemonProps.getProperties();
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            
            // 폴더01 Job & Trigger
            JobDetail job = JobBuilder.newJob(ApiCollectionDaemon.class)
                .withIdentity("jobName", Scheduler.DEFAULT_GROUP)
                .build();
            
            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trggerName", Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule((String)propsMap.get("period01")))// 매월 1일 마다
                .build();
            
            
            // 폴더02 Job & Trigger
            JobDetail job2 = JobBuilder.newJob(ApiCollectionDaemon2.class) // 다른 Job 클래스
                .withIdentity("job2", Scheduler.DEFAULT_GROUP)
                .build();
           
            
            Trigger trigger2 = TriggerBuilder.newTrigger()
                .withIdentity("trigger2", Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period02"))) // 다른 스케줄
                .build();
            
            
            // 폴더03 Job & Trigger
            JobDetail job3 = JobBuilder.newJob(ApiCollectionDaemon3.class) // 다른 Job 클래스
                .withIdentity("job3", Scheduler.DEFAULT_GROUP)
                .build();
           
            
            Trigger trigger3 = TriggerBuilder.newTrigger()
                .withIdentity("trigger3", Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period03"))) // 다른 스케줄
                .build();
            
            
            // 폴더04 Job & Trigger
            JobDetail job4 = JobBuilder.newJob(ApiCollectionDaemon4.class) // 다른 Job 클래스
                .withIdentity("job4", Scheduler.DEFAULT_GROUP)
                .build();
           
            
            Trigger trigger4 = TriggerBuilder.newTrigger()
                .withIdentity("trigger4", Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period04"))) // 다른 스케줄
                .build();
            
            
            // 폴더05 Job & Trigger
            JobDetail job5 = JobBuilder.newJob(ApiCollectionDaemon5.class) // 다른 Job 클래스
                .withIdentity("job5", Scheduler.DEFAULT_GROUP)
                .build();
           
            
            Trigger trigger5 = TriggerBuilder.newTrigger()
                .withIdentity("trigger5", Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period05"))) // 다른 스케줄
                .build();
                        
            scheduler.scheduleJob(job, trigger);
            scheduler.scheduleJob(job2, trigger2);
            scheduler.scheduleJob(job3, trigger3);
            scheduler.scheduleJob(job4, trigger4);
            scheduler.scheduleJob(job5, trigger5);
            
            scheduler.start();
        } catch(Exception e) {
            e.printStackTrace();
        }        
    }

}
