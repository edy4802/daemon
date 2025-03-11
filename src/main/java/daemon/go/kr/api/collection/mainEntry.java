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
            
            JobDetail job = JobBuilder.newJob(ApiCollectionDaemon.class)
                .withIdentity("jobName", Scheduler.DEFAULT_GROUP)
                .build();
            
            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trggerName", Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule((String)propsMap.get("OPM1")))// 매월 1일 마다
                .build();
            
            
            // 두 번째 Job & Trigger
            JobDetail job2 = JobBuilder.newJob(ApiCollectionDaemon2.class) // 다른 Job 클래스
                .withIdentity("job2", Scheduler.DEFAULT_GROUP)
                .build();
           
            
            Trigger trigger2 = TriggerBuilder.newTrigger()
                .withIdentity("trigger2", Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("OPM2"))) // 다른 스케줄
                .build();
                        
            scheduler.scheduleJob(job, trigger);
            scheduler.scheduleJob(job2, trigger2);
            
            scheduler.start();
        } catch(Exception e) {
            e.printStackTrace();
        }        
    }

}
