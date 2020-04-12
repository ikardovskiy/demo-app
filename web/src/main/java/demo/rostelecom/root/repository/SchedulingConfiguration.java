package demo.rostelecom.root.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name="app.cache.enable")
public class SchedulingConfiguration {
    @Autowired
    TaskScheduler scheduler;

    @Autowired
    PhoneCodeRepositoryWrapper refreshService;

    @Value("${app.cache.delay}")
    long reloadDelay;


    @PostConstruct
    public void init(){
        scheduler.scheduleWithFixedDelay(()->refreshService.refresh().block(),reloadDelay);
    }

}
