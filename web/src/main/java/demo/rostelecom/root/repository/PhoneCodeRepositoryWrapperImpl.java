package demo.rostelecom.root.repository;

import demo.rostelecom.root.model.PhoneCode;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Setter(AccessLevel.PRIVATE)
public class PhoneCodeRepositoryWrapperImpl implements PhoneCodeRepositoryWrapper {

    private final demo.rostelecom.root.dao.PhoneCodeRepository remoteRepository;

    private final boolean caching;

    private List<PhoneCodeIdx> phoneCodes = Collections.emptyList();

    public PhoneCodeRepositoryWrapperImpl(
            demo.rostelecom.root.dao.PhoneCodeRepository remoteRepository,
            @Value("${app.cache.enable}") boolean caching) {
        this.remoteRepository = remoteRepository;
        this.caching = caching;
    }

    @Override
    public Flux<PhoneCode> findByCountryContains(String country) {
        if(caching){
            var searchPattern = country.toUpperCase();
            return  Flux.fromStream(phoneCodes.stream().
                    filter(p->p.getUpperCountry().contains(searchPattern)))
                    .map(PhoneCodeIdx::getPhoneCode);
        }
        else {
            return remoteRepository.findByCountryContains(country);
        }
    }

    @Override
    public Mono<Void> refresh() {
        if(!caching)
            return Mono.error(new IllegalStateException("Невозможно обновление кеша - кеширование отключено"));
        return
                remoteRepository
                        .findAll()
                        .map(this::convertToPhoneCodeIdx)
                        .collectList()
                        .doOnNext( this::setPhoneCodes)
                        .then();
    }

    PhoneCodeIdx convertToPhoneCodeIdx(PhoneCode p){
        return PhoneCodeIdx.builder()
                .phoneCode(p)
                .upperCountry(Optional.ofNullable(p.getCountry()).map(String::toUpperCase).orElse(""))
                .build();
    }
    @Data
    @AllArgsConstructor
    @Builder
    static class PhoneCodeIdx{
        PhoneCode phoneCode;
        String upperCountry;
    }
}
