package demo.rostelecom.root.integration.controller;

import demo.rostelecom.root.integration.loader.LoaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@ContextConfiguration(classes = {IntegrationControllerTest.LocalConfiguration.class})
class IntegrationControllerTest {

    @MockBean
    private LoaderService loaderService;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void test_swagger_page_exists() {
        webClient.get()
                .uri("/swagger-ui.html")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void test_swagger_api_docs_exists() {
        webClient.get()
                .uri("/v2/api-docs")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }


    @Test
    public void test_searchCodes_success() {

        when(loaderService.loadCodes()).
                thenReturn(Mono.empty());

        webClient.post()
                .uri("/integration/load")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful();

        verify(loaderService, times(1)).loadCodes();
    }

    @Configuration
    @ComponentScan
    public static class LocalConfiguration {
    }
}