package demo.rostelecom.root.integration.loader;

import demo.rostelecom.root.model.PhoneCode;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;

import static demo.rostelecom.root.integration.loader.PhoneCodeRoute.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(webEnvironment = NONE, properties = {"camel.springboot.tracing=true"})
@ContextConfiguration(classes = PhoneCodeRouteTest.PhoneRouteTestConfig.class)
@UseAdviceWith
public class PhoneCodeRouteTest {

    public static final PhoneCode PHONE_CODE_1 = PhoneCode.builder()
            .name("BD")
            .country("Bangladesh")
            .code("880")
            .build();
    public static final PhoneCode PHONE_CODE_2 = PhoneCode.builder()
            .name("BD2")
            .country("Bangladesh2")
            .code("8801")
            .build();
    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    CamelContext context;

    @MockBean(name = "repositoryFacade")
    RepositoryFacade repositoryFacade;

  //  @Ignore
    @DirtiesContext
    @Test
    public void test1() throws Exception {
        Mockito.doNothing().when(repositoryFacade).save(any());
        Mockito.doNothing().when(repositoryFacade).delete(any());
        when(repositoryFacade.getAll()).thenReturn(Collections.emptyList());
        context.removeRouteDefinition(context.getRouteDefinition(ROUTE_LOADER_TIMER));
        context.start();
        producerTemplate.sendBody(DIRECT_LOADER, null);
        Mockito.verify(repositoryFacade, Mockito.times(1)).save(any());
    }

    @DirtiesContext
    @Test
    public void test_mock_http_endpoints() throws Exception {
        Mockito.doNothing().when(repositoryFacade).save(any());
        Mockito.doNothing().when(repositoryFacade).delete(any());
        when(repositoryFacade.getAll()).thenReturn(
                List.of(PHONE_CODE_1, PHONE_CODE_2)
        );
        context.getRouteDefinition(ROUTE_LOADER).adviceWith(context, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("{{app.source.baseurl}}names.json*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:countries");
                interceptSendToEndpoint("{{app.source.baseurl}}phone.json*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:phones");
            }
        });
        context.removeRouteDefinition(context.getRouteDefinition(ROUTE_LOADER_TIMER));
        context.start();
        final MockEndpoint countries = context.getEndpoint("mock:countries", MockEndpoint.class);
        countries.whenAnyExchangeReceived(exchange -> exchange.getMessage().setBody("{\"BD\": \"Bangladesh\"}"));
        final MockEndpoint phones = context.getEndpoint("mock:phones", MockEndpoint.class);
        phones.whenAnyExchangeReceived(exchange -> exchange.getMessage().setBody("{\"BD\": \"880\", \"BE\": \"32\"}"));
        NotifyBuilder notifyBuilder = new NotifyBuilder(context).whenCompleted(1).create();
        producerTemplate.sendBody(DIRECT_LOADER, "Hello");
        assertTrue(notifyBuilder.matches());
        Mockito.verify(repositoryFacade, Mockito.times(1)).save(List.of(PHONE_CODE_1));
        Mockito.verify(repositoryFacade, Mockito.times(1)).delete(List.of(PHONE_CODE_2));
        Mockito.verify(repositoryFacade, Mockito.times(1)).getAll();
    }


    @EnableAutoConfiguration
    @ComponentScan
    @Configuration
    public static class PhoneRouteTestConfig {

    }
}
