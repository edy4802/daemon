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
            if ("Y".equals(propsMap.get("use01"))) {	// Y/N 체크를 통한 폴더 사용여부 확인
                JobDetail job = JobBuilder.newJob(ApiCollectionDaemon.class)
                    .withIdentity("job01", Scheduler.DEFAULT_GROUP)
                    .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger01", Scheduler.DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period01")))
                    .build();

                scheduler.scheduleJob(job, trigger);
            }
            
            
            // 폴더02 Job & Trigger
            if ("Y".equals(propsMap.get("use02"))) {
                JobDetail job2 = JobBuilder.newJob(ApiCollectionDaemon2.class)
                    .withIdentity("job02", Scheduler.DEFAULT_GROUP)
                    .build();

                Trigger trigger2 = TriggerBuilder.newTrigger()
                    .withIdentity("trigger02", Scheduler.DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period02")))
                    .build();

                scheduler.scheduleJob(job2, trigger2);
            }
            
            
            // 폴더03 Job & Trigger
            if ("Y".equals(propsMap.get("use03"))) {
                JobDetail job3 = JobBuilder.newJob(ApiCollectionDaemon3.class)
                    .withIdentity("job03", Scheduler.DEFAULT_GROUP)
                    .build();

                Trigger trigger3 = TriggerBuilder.newTrigger()
                    .withIdentity("trigger03", Scheduler.DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period03")))
                    .build();

                scheduler.scheduleJob(job3, trigger3);
            }

            
            
            // 폴더04 Job & Trigger
            if ("Y".equals(propsMap.get("use04"))) {
                JobDetail job4 = JobBuilder.newJob(ApiCollectionDaemon4.class)
                    .withIdentity("job04", Scheduler.DEFAULT_GROUP)
                    .build();

                Trigger trigger4 = TriggerBuilder.newTrigger()
                    .withIdentity("trigger04", Scheduler.DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period04")))
                    .build();

                scheduler.scheduleJob(job4, trigger4);
            }
            
            
            // 폴더05 Job & Trigger
            if ("Y".equals(propsMap.get("use05"))) {
                JobDetail job5 = JobBuilder.newJob(ApiCollectionDaemon5.class)
                    .withIdentity("job05", Scheduler.DEFAULT_GROUP)
                    .build();

                Trigger trigger5 = TriggerBuilder.newTrigger()
                    .withIdentity("trigger05", Scheduler.DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule((String) propsMap.get("period05")))
                    .build();

                scheduler.scheduleJob(job5, trigger5);
            }
                        
            
            scheduler.start();
        } catch(Exception e) {
            e.printStackTrace();
        }        
    }

}
