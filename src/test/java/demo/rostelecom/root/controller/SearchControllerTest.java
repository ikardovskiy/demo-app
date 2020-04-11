package demo.rostelecom.root.controller;

import demo.rostelecom.root.integration.service.LoaderService;
import demo.rostelecom.root.model.PhoneCode;
import demo.rostelecom.root.service.CodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;

import static demo.rostelecom.root.controller.ApiTestUtils.loadResource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@ContextConfiguration(classes = {SearchControllerTest.LocalConfiguration.class})
class SearchControllerTest {

    @MockBean
    private CodeService codeService;

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
    public void test_searchCodes_success() throws IOException {

        final var expectedJson = loadResource(this.getClass(), "codeSearchResponse.json");

        when(codeService.searchCodes(eq("123"))).
                thenReturn(Mono.just(Collections.singletonList(
                        PhoneCode.builder()
                                .code("BE")
                                .name("Belgium")
                                .country("BLG")
                                .build()
                )));

        webClient.get()
                .uri("/rest/code?country={country}", "123")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().json(expectedJson);

        verify(codeService, times(1)).searchCodes(eq("123"));
    }

    @Test
    public void test_searchCodes_badRequest() {
        webClient.get()
                .uri("/rest/code")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void test_searchCodes_tooShort() {
        final var expectedErrorMessage = "Empty value is not acceptable";
        when(codeService.searchCodes(eq(""))).
                thenThrow(new IllegalArgumentException(expectedErrorMessage));
        webClient.get()
                .uri("/rest/code?country=")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectBody(String.class).isEqualTo(expectedErrorMessage);
    }


    @Configuration
    @ComponentScan
    public static class LocalConfiguration {
    }
}