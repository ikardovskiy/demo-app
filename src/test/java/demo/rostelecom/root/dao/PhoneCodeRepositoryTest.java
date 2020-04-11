package demo.rostelecom.root.dao;


import demo.rostelecom.root.model.PhoneCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = PhoneCodeRepositoryTest.LocalConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"logging.level.org.springframework.data.elasticsearch.client.WIRE=trace"})

class PhoneCodeRepositoryTest {

    static ElasticsearchContainer container;

    @Autowired
    PhoneCodeRepository repository;

    @BeforeAll
    public static void setUp(){
        container = new ElasticsearchContainer("elasticsearch:6.6.2")
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("[elastic container]"))
        .withEnv("discovery.type","single-node");
        container.start();
        System.setProperty("spring.data.elasticsearch.client.reactive.endpoints",container.getHttpHostAddress());
    }

    @AfterAll
    public static void tearDown(){
        container.close();
    }

    @BeforeEach
    public void clearData(){
        repository.deleteAll().block();
    }

    @Test
    public void test1(){
        PhoneCode phoneCode1 = PhoneCode.builder()
                .name("MA")
                .country("Malasia")
                .build();
        PhoneCode phoneCode2 = PhoneCode.builder()
                .name("TU")
                .country("Turkey")
                .build();
        repository.save(phoneCode1).block();
        repository.save(phoneCode2).block();
        final var phoneCodeList = repository.findByCountryContains("tu").collectList().block();
        final var expectedPhoneCodeList = Arrays.asList(phoneCode2);
        assertEquals(expectedPhoneCodeList,phoneCodeList);
    }


    @Configuration
    @EnableAutoConfiguration
    @EnableReactiveElasticsearchRepositories
    public static class LocalConfiguration {



    }
}