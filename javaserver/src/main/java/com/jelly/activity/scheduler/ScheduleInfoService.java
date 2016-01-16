package com.jelly.activity.scheduler;


import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailBean;

import com.dol.cdf.common.TimeUtil;

public class ScheduleInfoService {
	
	
	private Scheduler scheduler;
	// 设值注入，通过setter方法传入被调用者的实例scheduler  
    public void setScheduler(Scheduler scheduler) {     
        this.scheduler = scheduler;     
    }    
    
    public void loadJob() throws SchedulerException, ParseException{
    	QuartzTestBean tb = new QuartzTestBean();
    	JobDetailBean jobDetail0 = new JobDetailBean();
		jobDetail0.setJobClass(DummyJob.class);
		jobDetail0.setBeanName("myJob0");
		Map jobData = new HashMap();
		jobData.put("testBean", tb);
		jobDetail0.setJobDataAsMap(jobData);
		jobDetail0.afterPropertiesSet();

        // 运行时可通过动态注入的scheduler得到trigger，注意采用这种注入方式在有的项目中会有问题，如果遇到注入问题，可以采        取在运行方法时候，获得bean来避免错误发生。  
        CronTriggerBean trigger0 = new CronTriggerBean();
        trigger0.setBeanName("myTrigger0");
        trigger0.setJobDetail(jobDetail0);
        // 如果相等，则表示用户并没有重新设定数据库中的任务时间，这种情况不需要重新rescheduleJob 也不需要去执行任务 
        trigger0.setCronExpression("0 5 13 14 7 ?");  
        
        System.out.println("kai ......"+TimeUtil.formatDateLong(trigger0.getNextFireTime().getTime()));
        try {
			trigger0.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
        scheduler.rescheduleJob("testTrigger", Scheduler.DEFAULT_GROUP, trigger0);  

    }
    
    
    public static class DummyJob implements Job {

		private static int param;

		private static int count;

		public void setParam(int value) {
			if (param > 0) {
				throw new IllegalStateException("Param already set");
			}
			param = value;
		}

		@Override
		public synchronized void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
			count++;
			System.out.println("XXXXXXXXXXX");
		}
	}

    
    public void loadJob1(){
        JobDetail jobDetail = new JobDetail("CronTirgger",Scheduler.DEFAULT_GROUP, CronTrigger.class);
        jobDetail.getJobDataMap().put(" type ", " FULL ");
        CronTrigger trigger = new CronTrigger("CronTirgger",Scheduler.DEFAULT_GROUP);
        /**这里你可以写一个专门获取表达式的工具类 */
        try {
			trigger.setCronExpression("0 40 8 * * ?");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	

}
