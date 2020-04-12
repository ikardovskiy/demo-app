package demo.rostelecom.root.integration.service;

import reactor.core.publisher.Mono;

public interface LoaderService {
    Mono<Void> loadCodes();
}
