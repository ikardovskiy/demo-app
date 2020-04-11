package demo.rostelecom.root.integration.dao;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@WebFluxTest(properties = "debug=true")
@AutoConfigureWebClient
@ContextConfiguration(classes = CountryInfoDAOImplTest.LocalConfig.class)
class CountryInfoDAOImplTest {

    @Autowired
    WebClient.Builder builder;

    public static MockWebServer mockWebServer;

    @BeforeAll
    public static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }



    @Test
    public void test_getNames(){
        String baseUrl = mockWebServer.url("/").toString();
        final var countryInfoDAO = new CountryInfoDAOImpl(baseUrl, builder);
        var expectedJson = "{\"BD\": \"Bangladesh\", \"BE\": \"Belgium\"}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedJson).addHeader("Content-Type", "application/json"));
        Map<String, String> expectedReponse = Map.of("BD","Bangladesh","BE","Belgium");
        StepVerifier.create(countryInfoDAO.getNames())
                .expectNext(expectedReponse).verifyComplete();
    }

    @Test
    public void test_getPhones(){
        String baseUrl = mockWebServer.url("/").toString();
        final var countryInfoDAO = new CountryInfoDAOImpl(baseUrl, builder);
        var expectedJson = "{\"BD\": \"880\", \"BE\": \"32\"}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedJson).addHeader("Content-Type", "application/json"));
        Map<String, String> expectedReponse = Map.of("BD","880","BE","32");
        StepVerifier.create(countryInfoDAO.getPhones())
                .expectNext(expectedReponse).verifyComplete();
    }

    @Configuration
    public static class LocalConfig {
    }
}