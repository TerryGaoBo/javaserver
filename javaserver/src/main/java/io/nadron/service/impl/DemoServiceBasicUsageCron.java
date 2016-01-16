package io.nadron.service.impl;

import org.springframework.scheduling.annotation.Scheduled;

public class DemoServiceBasicUsageCron 
{	
	@Scheduled(cron="*/5 * * * * ?")
	public void demoServiceMethod()
	{
		//System.out.println("Method executed at every 5 seconds. Current time is :: "+ new Date());
	}
	
	//每天12点或者18点触发
	@Scheduled(cron="0 0 12,18 * * ?")
	public void demoServiceMethod1()
	{
		//System.out.println("Method executed at every 5 seconds. Current time is :: "+ new Date());
	}
}
