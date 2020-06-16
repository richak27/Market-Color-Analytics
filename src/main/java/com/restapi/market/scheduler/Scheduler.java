package com.restapi.market.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.restapi.market.service.CompanyService;

@Component
public class Scheduler {
	
	@Autowired
	private CompanyService companyService;
	
	@Scheduled(cron = "${cron.expression}")
	public void dailyUpdate() {
		companyService.dailyUpdateAll();
	}

}
