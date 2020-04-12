package demo.rostelecom.root.dao;

import demo.rostelecom.root.model.PhoneCode;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface PhoneCodeRepository extends ReactiveSortingRepository<PhoneCode, String> {

    Flux<PhoneCode> findByCountryContains(String country);

}
