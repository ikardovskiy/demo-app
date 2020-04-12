package demo.rostelecom.root.integration.dao;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface CountryInfoDAO {

    Mono<Map<String, String>> getNames();
    Mono<Map<String, String>> getPhones();
}
