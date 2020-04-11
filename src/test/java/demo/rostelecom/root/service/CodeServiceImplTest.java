package demo.rostelecom.root.service;

import demo.rostelecom.root.dao.PhoneCodeRepository;
import demo.rostelecom.root.model.PhoneCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeServiceImplTest {

    @Mock
    PhoneCodeRepository phoneCodeRepository;
    private CodeServiceImpl codeService;

    @BeforeEach
    public void setUp(){
        codeService = new CodeServiceImpl(phoneCodeRepository);
    }

    @Test
    public void test_validateAndConvertCountry_success(){
        StepVerifier.create(codeService.validateAndConvertCountry("abc"))
                .expectNext("abc").verifyComplete();
    }

    @Test
    public void test_validateAndConvertCountry_success_remove_whitespace(){
        StepVerifier.create(codeService.validateAndConvertCountry(" a c \r\n"))
                .expectNext("ac").verifyComplete();
    }

    @Test
    public void test_validateAndConvertCountry_error_too_few(){
        StepVerifier.create(codeService.validateAndConvertCountry("a"))
                .verifyErrorMatches(e->e.getMessage().startsWith("В паттерне должно быть более одного символа"));
    }

    @Test
    public void test_validateAndConvertCountry_error_empty(){
        StepVerifier.create(codeService.validateAndConvertCountry(" "))
                .verifyErrorMatches(e->e.getMessage().startsWith("В паттерне поиска должен быть текст"));
    }

    @Test
    public void test_searchCodes(){

        var phoneCode = PhoneCode.builder().name("test").build();

        when(phoneCodeRepository.findByCountryContains(eq("GB")))
                .thenReturn(Flux.just(phoneCode));

        StepVerifier.create(codeService.searchCodes("GB"))
                .expectNext(Arrays.asList(phoneCode)).verifyComplete();

        verify(phoneCodeRepository,times(1)).findByCountryContains(eq("GB"));

    }
}