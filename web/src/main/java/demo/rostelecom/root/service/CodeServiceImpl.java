package demo.rostelecom.root.service;


import demo.rostelecom.root.model.PhoneCode;
import demo.rostelecom.root.repository.PhoneCodeRepositoryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class CodeServiceImpl implements CodeService {

    private final PhoneCodeRepositoryWrapper phoneCodeRepository;

    public CodeServiceImpl(PhoneCodeRepositoryWrapper phoneCodeRepository) {
        this.phoneCodeRepository = phoneCodeRepository;
    }

    @Override
    public Mono<List<PhoneCode>> searchCodes(String country) {
        return validateAndConvertCountry(country)
                .flatMapMany(phoneCodeRepository::findByCountryContains)
                .collectList()
                .doOnNext(s->log.trace("Request: {} Response: {}",country,s));
    }

    Mono<String> validateAndConvertCountry(String country){
       return Mono.just(country).flatMap(s-> StringUtils.hasText(s)?Mono.just(s):Mono.error(new IllegalArgumentException("В паттерне поиска должен быть текст")))
                .map(s->s.replaceAll("\\s",""))
               .doOnNext(s->log.trace("pattern after whitespace replacement: {}",s))
               .flatMap(s->s.length()>1?Mono.just(s):Mono.error(new IllegalArgumentException("В паттерне должно быть более одного символа")));

    }
}
