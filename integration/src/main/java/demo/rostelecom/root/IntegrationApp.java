package demo.rostelecom.root;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

@SpringBootApplication
@EnableReactiveElasticsearchRepositories
public class IntegrationApp {
    public static void main(String[] args) {
        SpringApplication.run(IntegrationApp.class, args);
    }
}
