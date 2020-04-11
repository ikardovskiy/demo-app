package demo.rostelecom.root.integration.service;

import demo.rostelecom.root.dao.PhoneCodeRepository;
import demo.rostelecom.root.integration.dao.CountryInfoDAO;
import demo.rostelecom.root.model.PhoneCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("UnnecessaryLocalVariable")
@Service
public class LoaderServiceImpl implements LoaderService {

    private final PhoneCodeRepository phoneCodeRepository;
    private final CountryInfoDAO countryInfoDAO;

    public LoaderServiceImpl(PhoneCodeRepository phoneCodeRepository, CountryInfoDAO countryInfoDAO) {
        this.phoneCodeRepository = phoneCodeRepository;
        this.countryInfoDAO = countryInfoDAO;
    }

    @Scheduled
    @Override
    public Mono<Void> loadCodes() {
        return Mono.zip(countryInfoDAO.getNames(), countryInfoDAO.getPhones())
                .map(LoaderServiceImpl::covertToPhoneCodes)
                .flatMap(this::loadPhoneCodes);
    }

    Mono<Void> loadPhoneCodes(Map<String, PhoneCode> phoneCodeIdx){
        final var phoneCodes = phoneCodeIdx.values();
        final var newCodeNames = phoneCodeIdx.keySet();
        return createOrUpdate(phoneCodes)
                        .then(deleteAbsent(newCodeNames));
    }

    Flux<PhoneCode> createOrUpdate(Collection<PhoneCode> phoneCodes){
        return Flux.fromIterable(phoneCodes).flatMap(phoneCodeRepository::save,100);
    }

    Mono<Void> deleteAbsent(Set<String> newCodeNames){
        return phoneCodeRepository.findAll()
                .filter(c->!newCodeNames.contains(c.getName()))
                .flatMap(phoneCodeRepository::delete).then();
    }

    static Map<String, PhoneCode> covertToPhoneCodes(Tuple2<Map<String, String>, Map<String, String>> rawData) {
        final var names = rawData.getT1();
        final var phones = rawData.getT2();
        final var phoneCodeMap = names.entrySet().stream().map(entry -> PhoneCode.builder()
                .name(entry.getKey())
                .country(entry.getValue())
                .code(phones.getOrDefault(entry.getKey(), null))
                .build()).collect(Collectors.toMap(PhoneCode::getName, Function.identity()));
        return phoneCodeMap;
    }

}
