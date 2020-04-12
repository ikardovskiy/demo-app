package demo.rostelecom.root.repository;

import demo.rostelecom.root.model.PhoneCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PhoneCodeRepositoryWrapper {
    Flux<PhoneCode> findByCountryContains(String country);
    Mono<Void> refresh();
}
