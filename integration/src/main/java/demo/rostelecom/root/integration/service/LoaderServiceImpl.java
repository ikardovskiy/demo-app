package demo.rostelecom.root.integration.service;

import demo.rostelecom.root.dao.PhoneCodeRepository;
import demo.rostelecom.root.integration.dao.CountryInfoDAO;
import demo.rostelecom.root.model.PhoneCode;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class LoaderServiceImpl implements LoaderService {

    private final PhoneCodeRepository phoneCodeRepository;
    private final CountryInfoDAO countryInfoDAO;

    public LoaderServiceImpl(PhoneCodeRepository phoneCodeRepository, CountryInfoDAO countryInfoDAO) {
        this.phoneCodeRepository = phoneCodeRepository;
        this.countryInfoDAO = countryInfoDAO;
    }


    @Override
    public Mono<Void> loadCodes() {
        log.info("Load phone codes: START");
        return Mono.zip(
                countryInfoDAO.getNames().
                        doOnSuccess(c->log.debug("Load phone codes: Loaded names")).
                        doOnError(t->log.error("Load phone codes: Names loading failed: {}",t.getMessage()))
                ,
                countryInfoDAO.getPhones()
                        .doOnSuccess(c->log.debug("Load phone codes: Loaded phones"))
                        .doOnError(t->log.error("Load phone codes: Phones loading failed: {}",t.getMessage())))
                .map(LoaderServiceImpl::covertToPhoneCodes)
                .flatMap(this::loadPhoneCodes)
                .doOnSuccess(c->log.info("Load phone codes: SUCCESS"))
                .doOnError(t->log.error("Load phone codes: ERROR"))
                .doFinally(c->log.info("Load phone codes: COMPLETED"));
    }

    Mono<Void> loadPhoneCodes(Map<String, PhoneCode> phoneCodeIdx){
        final var phoneCodes = phoneCodeIdx.values();
        final var newCodeNames = phoneCodeIdx.keySet();
        return createOrUpdate(phoneCodes)
                    .doOnComplete(()->log.debug("Load phone codes: Documents crated and updated"))
                    .doOnError(t->log.error("Load phone codes: Documents update failed: {}",t.getMessage()))
                    .then(
                            deleteAbsent(newCodeNames)
                                    .doOnSuccess(c->log.debug("Load phone codes: Documents deleted"))
                                    .doOnError(t->log.error("Load phone codes: Documents deletion failed: {}",t.getMessage()))
                    );

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
                .code(phones.get(entry.getKey()))
                .build()).collect(Collectors.toMap(PhoneCode::getName, Function.identity()));
        return phoneCodeMap;
    }

}
