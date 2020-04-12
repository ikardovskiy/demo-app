package demo.rostelecom.root.integration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@Configuration
@EnableScheduling
public class SchedulingConfiguration {

    @Autowired
    TaskScheduler scheduler;

    @Autowired
    LoaderService loaderService;

    @Value("${app.integration.reload.delay}")
    long reloadDelay;



    @PostConstruct
    public void init(){
        scheduler.scheduleWithFixedDelay(()->loaderService.loadCodes().block(),reloadDelay);
    }



}
