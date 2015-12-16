package javaserver.test;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;

public class TestScheduled {
	
	public TestScheduled(){
		
	}
	
	// 每周日晚上12：00 10 分执行
	@Scheduled(cron = "0 26 19 * * ?")
	public void clearTeamMemberPaiQianCiShu() {
		//			例  "0 0 12 ? * WED" 在每星期三下午12:00 执行,
		Date date = new Date();
		System.out.print(date.toString());
	}
}

