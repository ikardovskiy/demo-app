package demo.rostelecom.root.service;

import demo.rostelecom.root.model.PhoneCode;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CodeService {
    Mono<List<PhoneCode>> searchCodes(String country);
}
