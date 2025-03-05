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
                .withSchedule(CronScheduleBuilder.cronSchedule((String)propsMap.get("OPM")))// 매월 1일 마다
                .build();
                        
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch(Exception e) {
            e.printStackTrace();
        }        
    }

}
