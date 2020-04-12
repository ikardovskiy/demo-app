package demo.rostelecom.root.repository;

import demo.rostelecom.root.dao.PhoneCodeRepository;
import demo.rostelecom.root.model.PhoneCode;
import demo.rostelecom.root.repository.PhoneCodeRepositoryWrapperImpl.PhoneCodeIdx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhoneCodeRepositoryWrapperImplTest {

    @Mock
    private PhoneCodeRepository remoteRepository;
    public static final PhoneCode PHONE_1 = PhoneCode.builder()
            .code("111")
            .country("Usa")
            .name("US")
            .build();
    public static final PhoneCode PHONE_2 = PhoneCode.builder()
            .code("666")
            .country("North Korea")
            .name("NK")
            .build();
    public static final PhoneCode PHONE_3 = PhoneCode.builder()
            .code("777")
            .country(null)
            .name("JB")
            .build();
    public static final List<PhoneCodeIdx> PHONE_CODE_IDXES = List.of(PhoneCodeIdx.builder()
                    .upperCountry("USA")
                    .phoneCode(PHONE_1)
                    .build(),
            PhoneCodeIdx.builder()
                    .upperCountry("NORTH KOREA")
                    .phoneCode(PHONE_2)
                    .build(),
            PhoneCodeIdx.builder()
                    .upperCountry("")
                    .phoneCode(PHONE_3)
                    .build()

    );

    @Test
    public void test_refresh_error() {
        var wrapper = new PhoneCodeRepositoryWrapperImpl(remoteRepository, false);
        StepVerifier.create(wrapper.refresh())
                .verifyError(IllegalStateException.class);
    }

    @Test
    public void test_refresh_success() {
        when(remoteRepository.findAll())
                .thenReturn(Flux.just(
                        PHONE_1,
                        PHONE_2,
                        PHONE_3));
        var wrapper = new PhoneCodeRepositoryWrapperImpl(remoteRepository, true);
        StepVerifier.create(wrapper.refresh())
                .expectNext().verifyComplete();
        verify(remoteRepository, times(1)).findAll();
        assertEquals(PHONE_CODE_IDXES, wrapper.getPhoneCodes());
    }

    @Test
    public void test_findByCountryContains_noCache() {
        when(remoteRepository.findByCountryContains(eq("test")))
                .thenReturn(Flux.empty());
        var wrapper = new PhoneCodeRepositoryWrapperImpl(remoteRepository, false);
        StepVerifier.create(wrapper.findByCountryContains("test"))
                .verifyComplete();
        verify(remoteRepository, times(1))
                .findByCountryContains(eq("test"));

    }

    @Test
    public void test_findByCountryContains_cache() {
        var wrapper = new PhoneCodeRepositoryWrapperImpl(remoteRepository, true);
        wrapper.setPhoneCodes(PHONE_CODE_IDXES);
        StepVerifier.create(wrapper.findByCountryContains("or"))
                .expectNext(PHONE_2)
                .verifyComplete();
    }

}