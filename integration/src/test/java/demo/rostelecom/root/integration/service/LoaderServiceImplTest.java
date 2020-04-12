package demo.rostelecom.root.integration.service;

import demo.rostelecom.root.dao.PhoneCodeRepository;
import demo.rostelecom.root.integration.dao.CountryInfoDAO;
import demo.rostelecom.root.model.PhoneCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoaderServiceImplTest {

    @Mock
    PhoneCodeRepository phoneCodeRepository;

    @Mock
    CountryInfoDAO countryInfoDAO;

    LoaderServiceImpl loaderService;
    public static final Map<String, String> NAMES = Map.of("A", "A1", "B", "B1");
    public static final Map<String, String> PHONES = Map.of("A", "007", "C", "666");
    public static final PhoneCode PHONE_CODE_1 = PhoneCode.builder()
            .code("007")
            .name("A")
            .country("A1")
            .build();
    public static final PhoneCode PHONE_CODE_2 = PhoneCode.builder()
            .code(null)
            .name("B")
            .country("B1")
            .build();

    @BeforeEach
    public void beforeEach() {
        loaderService = new LoaderServiceImpl(phoneCodeRepository, countryInfoDAO);
    }

    @Test
    void test_covertToPhoneCodes() {

        Tuple2<Map<String, String>, Map<String, String>> sourceData = Tuples.of(NAMES, PHONES);

        Map<String, PhoneCode> expectedResult =
                Map.of("A", PHONE_CODE_1,
                        "B", PHONE_CODE_2);
        final var stringPhoneCodeMap = LoaderServiceImpl.covertToPhoneCodes(sourceData);
        assertEquals(expectedResult, stringPhoneCodeMap);
    }

    @Test
    public void test_createOrUpdate(){
        when(phoneCodeRepository.save(any()))
                .thenAnswer(t-> Mono.<PhoneCode>just(t.getArgument(0)));
        final var phoneCodes = Collections.singletonList(PHONE_CODE_1);
        StepVerifier.create(loaderService.createOrUpdate(phoneCodes))
        .expectNext(phoneCodes.get(0)).verifyComplete();

        verify(phoneCodeRepository, times(1))
                .save(any());
    }

    @Test
    public void test_deleteAbsent(){

        when(phoneCodeRepository.delete(eq(PHONE_CODE_1)))
                .thenReturn(Mono.empty());

        when(phoneCodeRepository.findAll())
                .thenReturn(Flux.just(PHONE_CODE_1, PHONE_CODE_2));

        StepVerifier.create(loaderService.deleteAbsent(Set.of("B")))
                .verifyComplete();

        verify(phoneCodeRepository, times(1))
                .delete(eq(PHONE_CODE_1));

        verify(phoneCodeRepository, times(1))
                .findAll();

    }

    @Test
    public void test_loadCodes() {
        when(this.countryInfoDAO.getNames()).thenReturn(Mono.just(NAMES));
        when(this.countryInfoDAO.getPhones()).thenReturn(Mono.just(PHONES));
        when(phoneCodeRepository.save(any()))
                .thenAnswer(t -> Mono.<PhoneCode>just(t.getArgument(0)));

        when(this.phoneCodeRepository.findAll()).thenReturn(Flux.just(PHONE_CODE_1, PHONE_CODE_2));

        StepVerifier.create(loaderService.loadCodes())
                .verifyComplete();

        verify(phoneCodeRepository, times(2))
                .save(any());
        verify(phoneCodeRepository, times(1))
                .findAll();
        verify(phoneCodeRepository, times(0))
                .delete(any());

    }

}